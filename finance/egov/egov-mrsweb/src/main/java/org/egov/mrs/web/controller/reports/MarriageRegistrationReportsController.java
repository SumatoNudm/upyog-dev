/* eGov suite of products aim to improve the internal efficiency,transparency,
   accountability and the service delivery of the government  organizations.

    Copyright (C) <2015>  eGovernments Foundation

    The updated version of eGov suite of products as by eGovernments Foundation
    is available at http://www.egovernments.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see http://www.gnu.org/licenses/ or
    http://www.gnu.org/licenses/gpl.html .

    In addition to the terms of the GPL license to be adhered to in using this
    program, the following additional terms are to be complied with:

        1) All versions of this program, verbatim or modified must carry this
           Legal Notice.

        2) Any misrepresentation of the origin of the material is prohibited. It
           is required that all modified versions of this material be marked in
           reasonable ways as different from the original version.

        3) This license does not grant any rights to any user of the program
           with regards to rights under trademark law for use of the trade names
           or trademarks of eGovernments Foundation.

  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.mrs.web.controller.reports;

import static org.egov.infra.web.utils.WebUtils.toJSON;
import static org.egov.mrs.application.MarriageConstants.BOUNDARY_TYPE;
import static org.egov.mrs.application.MarriageConstants.REVENUE_HIERARCHY_TYPE;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.egov.infra.admin.master.entity.Boundary;
import org.egov.infra.admin.master.service.BoundaryService;
import org.egov.infra.utils.DateUtils;
import org.egov.mrs.application.reports.service.MarriageRegistrationReportsService;
import org.egov.mrs.domain.entity.MaritalStatusReport;
import org.egov.mrs.domain.entity.MarriageCertificate;
import org.egov.mrs.domain.entity.MarriageRegistration;
import org.egov.mrs.domain.entity.MarriageRegistration.RegistrationStatus;
import org.egov.mrs.domain.entity.RegistrationCertificatesResultForReport;
import org.egov.mrs.domain.enums.MaritalStatus;
import org.egov.mrs.domain.service.MarriageRegistrationService;
import org.egov.mrs.masters.entity.MarriageAct;
import org.egov.mrs.masters.service.MarriageActService;
import org.egov.mrs.masters.service.MarriageRegistrationUnitService;
import org.egov.mrs.masters.service.ReligionService;
import org.egov.mrs.web.adaptor.MaritalStatusReportJsonAdaptor;
import org.egov.mrs.web.adaptor.MarriageRegistrationCertificateReportJsonAdaptor;
import org.egov.mrs.web.adaptor.MarriageRegistrationJsonAdaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Controller to show report of Registration status
 *
 * @author nayeem
 *
 */
@Controller
@RequestMapping(value = "/report")
public class MarriageRegistrationReportsController {

    private static final String[] RANGES = new String[] { "0-18", "19-25",
            "26-30", "31-35", "36-40", "40-45", "46-50", "50-100" };
    private static final String KEY_AGE = "age";
    private static final String KEY_HUSBANDCOUNT = "husbandcount";
    private static final String KEY_WIFECOUNT = "wifecount";
    private static final String KEY_MONTH = "month";
    private static final String KEY_REGCOUNT = "Registrationcount";
    private static final String KEY_ACT = "MarriageAct";

    @Autowired
    protected BoundaryService boundaryService;

    @Autowired
    private MarriageRegistrationService marriageRegistrationService;

    @Autowired
    private MarriageRegistrationReportsService marriageRegistrationReportsService;

    @Autowired
    private MarriageRegistrationUnitService marriageRegistrationUnitService;

    @Autowired
    private MarriageActService marriageActService;

    @Autowired
    private ReligionService religionService;

    @ModelAttribute("zones")
    public List<Boundary> getZonesList() {
        return boundaryService
                .getActiveBoundariesByBndryTypeNameAndHierarchyTypeName(
                        BOUNDARY_TYPE, REVENUE_HIERARCHY_TYPE);
    }

    private final Map<Integer, String> monthMap = DateUtils
            .getAllMonthsWithFullNames();

    @RequestMapping(value = "/registrationstatus", method = RequestMethod.GET)
    public String showReportForm(final Model model) {
        model.addAttribute("registration", new MarriageRegistration());
        model.addAttribute("status", RegistrationStatus.values());
        return "report-registrationstatus";
    }

    @RequestMapping(value = "/registrationstatus", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String search(final Model model,
            @ModelAttribute final MarriageRegistration registration)
            throws ParseException {
        final List<MarriageRegistration> searchResultList = marriageRegistrationService
                .searchRegistrationByStatus(registration, registration
                        .getStatus().getCode());
        return new StringBuilder("{ \"data\":")
                .append(toJSON(searchResultList, MarriageRegistration.class,
                        MarriageRegistrationJsonAdaptor.class)).append("}")
                .toString();
    }

    @RequestMapping(value = "/age-wise", method = RequestMethod.GET)
    public String newSearchForm(final Model model) {
        model.addAttribute("registration", new MarriageRegistration());
        model.addAttribute("yearlist", getPreviousyears());
        return "report-registration-agewise";
    }

    @RequestMapping(value = "/age-wise", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String searchAgeWise(@RequestParam("year") final int year,
            final Model model, @ModelAttribute final MarriageRegistration registration)
            throws ParseException {

        final HashMap<String, Integer> husbandAgeRangesCount = getCountByRange(marriageRegistrationReportsService
                .searchRegistrationOfHusbandAgeWise(year));
        final HashMap<String, Integer> wifeAgeRangesCount = getCountByRange(marriageRegistrationReportsService
                .searchRegistrationOfWifeAgeWise(year));

        final ArrayList<HashMap<String, Object>> result = new ArrayList<>();

        for (final String range : RANGES) {
            final HashMap<String, Object> rangeMap = new HashMap<>();
            rangeMap.put(KEY_AGE, range);
            rangeMap.put(
                    KEY_HUSBANDCOUNT,
                    husbandAgeRangesCount.get(range) != null ? husbandAgeRangesCount
                            .get(range) : 0);
            rangeMap.put(
                    KEY_WIFECOUNT,
                    wifeAgeRangesCount.get(range) != null ? wifeAgeRangesCount
                            .get(range) : 0);
            result.add(rangeMap);
        }

        final JsonArray jsonArray = (JsonArray) new Gson().toJsonTree(result,
                new TypeToken<List<HashMap<String, Object>>>() {

                    /**
             *
             */
                    private static final long serialVersionUID = 5562709025385195886L;
                }.getType());

        final JsonObject response = new JsonObject();
        response.add("data", jsonArray);
        return response.toString();
    }

    private HashMap<String, Integer> getCountByRange(final String[] inputs) {

        final HashMap<String, Integer> response = new HashMap<>();

        for (final String input : inputs) {
            final String[] values = input.split(","); // age,count -> [0] - age, [1] -
            // count
            final Integer age = Integer.valueOf(values[0]);
            for (final String range : RANGES)
                if (isInRange(range, age)) {
                    final int existingCount = response.get(range) != null ? response
                            .get(range) : 0;
                    response.put(range,
                            existingCount + Integer.valueOf(values[1]));
                    break;
                }
        }

        return response;
    }

    private boolean isInRange(final String ranges, final Integer input) {
        final String[] range = ranges.split("-");
        return input >= Integer.valueOf(range[0]) && input <= Integer
                .valueOf(range[1]);
    }

    @RequestMapping(value = "/age-wise/view/{year}/{applicantType}/{ageRange}", method = RequestMethod.GET)
    public String viewAgeWiseDetails(@PathVariable final int year,
            @PathVariable final String applicantType,
            @PathVariable final String ageRange, final Model model)
            throws IOException, ParseException {
        final List<MarriageRegistration> marriageRegistrations = marriageRegistrationReportsService
                .getAgewiseDetails(ageRange, applicantType, year);
        model.addAttribute("marriageRegistrations", marriageRegistrations);
        model.addAttribute("applicantType", applicantType);
        return "marriage-agewise-view";
    }

    @RequestMapping(value = "/certificatedetails", method = RequestMethod.GET)
    public String searchCertificatesForReport(final Model model) {
        model.addAttribute("certificate", new MarriageCertificate());
        return "registration-certificates-report";
    }

    @RequestMapping(value = "/certificatedetails", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String searchApprovedMarriageRecords(final Model model,
            @ModelAttribute final MarriageCertificate certificate)
            throws ParseException {
        final List<RegistrationCertificatesResultForReport> regCertificateResult = new ArrayList<RegistrationCertificatesResultForReport>();
        final List<Object[]> searchResultList = marriageRegistrationReportsService
                .searchMarriageRegistrationsForCertificateReport(certificate);
        for (final Object[] objects : searchResultList) {
            final RegistrationCertificatesResultForReport certificatesResultForReport = new RegistrationCertificatesResultForReport();
            certificatesResultForReport
                    .setRegistrationNo(objects[0] != null ? objects[0].toString() : "");
            certificatesResultForReport
                    .setDateOfMarriage(objects[1].toString());
            certificatesResultForReport.setRegistrationDate(objects[2]
                    .toString());
            certificatesResultForReport.setRejectReason(objects[3] != null ? objects[3].toString() : "");
            certificatesResultForReport
                    .setCertificateNo(objects[4] != null ? objects[4]
                            .toString() : "");
            certificatesResultForReport.setCertificateType(objects[5]
                    .toString());
            certificatesResultForReport.setCertificateDate(objects[6]
                    .toString());
            certificatesResultForReport.setZone(objects[7].toString());
            certificatesResultForReport.setHusbandName(objects[8].toString());
            certificatesResultForReport.setWifeName(objects[9].toString());
            certificatesResultForReport.setId(Long.valueOf(objects[10]
                    .toString()));
            regCertificateResult.add(certificatesResultForReport);
        }
        return new StringBuilder("{ \"data\":")
                .append(toJSON(regCertificateResult,
                        RegistrationCertificatesResultForReport.class,
                        MarriageRegistrationCertificateReportJsonAdaptor.class))
                .append("}").toString();
    }

    @RequestMapping(value = "/status-at-time-marriage", method = RequestMethod.GET)
    public String getStatusAtTimeOfMarriage(final Model model) {
        model.addAttribute("registration", new MarriageRegistration());
        model.addAttribute("maritalStatusList",
                Arrays.asList(MaritalStatus.values()));
        model.addAttribute("yearlist", getPreviousyears());
        return "statustime-ofmarriage-report";
    }

    @RequestMapping(value = "/status-at-time-marriage", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String searchStatusAtTimeOfMarriage(
            @RequestParam("year") final int year, final Model model,
            @ModelAttribute final MarriageRegistration registration)
            throws ParseException {
        final List<MaritalStatusReport> maritalStatusReports = new ArrayList<>();
        maritalStatusReports.addAll(putRecordsIntoHashMapByMonth(
                marriageRegistrationReportsService
                        .getHusbandCountByMaritalStatus(year), "husband"));
        maritalStatusReports.addAll(putRecordsIntoHashMapByMonth(
                marriageRegistrationReportsService
                        .getWifeCountByMaritalStatus(year), "wife"));
        return new StringBuilder("{ \"data\":")
                .append(toJSON(maritalStatusReports, MaritalStatusReport.class,
                        MaritalStatusReportJsonAdaptor.class)).append("}")
                .toString();
    }

    private List<MaritalStatusReport> putRecordsIntoHashMapByMonth(
            final List<String[]> recordList, final String applicantType) {
        final Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
        for (final Object[] category : recordList)
            if (map.containsKey(category[1])) {
                if (map.get(category[1]).containsKey(category[0]))
                    map.get(category[1]).put(String.valueOf(category[0]),
                            String.valueOf(category[2]));
                else {
                    final Map<String, String> subMap = new HashMap<>();
                    subMap.put(String.valueOf(category[0]),
                            String.valueOf(category[2]));
                    map.get(category[1]).put(String.valueOf(category[0]),
                            String.valueOf(category[2]));

                }
            } else {
                final Map<String, String> subMap = new HashMap<>();
                subMap.put(String.valueOf(category[0]),
                        String.valueOf(category[2]));
                map.put(String.valueOf(category[1]), subMap);

                map.get(category[1]).put("applicantType", applicantType);
            }
        final List<MaritalStatusReport> maritalStatusReports = new ArrayList<>();
        for (final Entry<String, Map<String, String>> resMap : map.entrySet()) {
            final MaritalStatusReport report = new MaritalStatusReport();
            report.setMonth(resMap.getKey());
            for (final Entry<String, String> valuesMap : resMap.getValue().entrySet()) {
                if (valuesMap.getValue().equalsIgnoreCase("husband")
                        || valuesMap.getValue().equalsIgnoreCase("wife"))
                    report.setApplicantType(applicantType);

                if (valuesMap.getKey().equalsIgnoreCase(
                        MaritalStatus.Married.toString()))
                    report.setMarried(valuesMap.getValue() != null ? valuesMap
                            .getValue() : "0");
                else if (valuesMap.getKey().equalsIgnoreCase(
                        MaritalStatus.Unmarried.toString()))
                    report.setUnmarried(valuesMap.getValue() != null ? valuesMap
                            .getValue() : "0");
                else if (valuesMap.getKey().equalsIgnoreCase(
                        MaritalStatus.Widower.toString()))
                    report.setWidower(valuesMap.getValue() != null ? valuesMap
                            .getValue() : "0");
                else if (valuesMap.getKey().equalsIgnoreCase(
                        MaritalStatus.Divorced.toString()))
                    report.setDivorced(valuesMap.getValue() != null ? valuesMap
                            .getValue() : "0");

            }
            maritalStatusReports.add(report);
        }

        return maritalStatusReports;
    }

    @RequestMapping(value = "/status-at-time-marriage/view/{year}/{month}/{applicantType}/{maritalStatus}", method = RequestMethod.GET)
    public String viewByMaritalStatus(@PathVariable final int year,
            @PathVariable final String month,
            @PathVariable final String applicantType,
            @PathVariable final String maritalStatus, final Model model)
            throws IOException, ParseException {
        final List<MarriageRegistration> marriageRegistrations = marriageRegistrationReportsService
                .getByMaritalStatusDetails(year, month, applicantType,
                        maritalStatus);
        model.addAttribute("marriageRegistrations", marriageRegistrations);
        model.addAttribute("applicantType", applicantType);
        return "status-timeofmrg-view";
    }

    @RequestMapping(value = "/datewiseregistration", method = RequestMethod.GET)
    public String showDatewiseReportForm(final Model model) {
        model.addAttribute("registration", new MarriageRegistration());
        model.addAttribute("marriageRegistrationUnit",
                marriageRegistrationUnitService.getActiveRegistrationunit());
        model.addAttribute("status", RegistrationStatus.values());
        return "report-datewiseregsitration";
    }

    @RequestMapping(value = "/datewiseregistration", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String showDatewiseReportresult(final Model model,
            @ModelAttribute final MarriageRegistration registration)
            throws ParseException {
        final List<MarriageRegistration> searchResultList = marriageRegistrationReportsService
                .searchRegistrationBydate(registration);
        final String result = new StringBuilder("{ \"data\":")
                .append(toJSON(searchResultList, MarriageRegistration.class,
                        MarriageRegistrationJsonAdaptor.class)).append("}")
                .toString();
        return result;
    }

    @RequestMapping(value = "/monthwiseregistration", method = RequestMethod.GET)
    public String showMonthwiseReportForm(final Model model) {
        model.addAttribute("registration", new MarriageRegistration());
        model.addAttribute("marriageRegistrationUnit",
                marriageRegistrationUnitService.getActiveRegistrationunit());
        model.addAttribute("status", RegistrationStatus.values());
        return "report-monthwiseregistration";
    }

    @RequestMapping(value = "/monthwiseregistration", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String showMonthwiseReportresult(final Model model,
            @ModelAttribute final MarriageRegistration registration)
            throws ParseException {
        final List<MarriageRegistration> searchResultList = marriageRegistrationReportsService
                .searchRegistrationBymonth(registration);
        return new StringBuilder("{ \"data\":")
                .append(toJSON(searchResultList, MarriageRegistration.class,
                        MarriageRegistrationJsonAdaptor.class)).append("}")
                .toString();
    }

    @RequestMapping(value = "/actwiseregistration", method = RequestMethod.GET)
    public String showActwiseReportForm(final Model model) {
        model.addAttribute("registration", new MarriageRegistration());
        model.addAttribute("acts", marriageActService.getActs());
        model.addAttribute("yearlist", getPreviousyears());
        return "report-actwiseregistration";
    }

    @RequestMapping(value = "/actwiseregistration", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String showActwiseReportresult(
            @RequestParam("year") final int year, final Model model,
            @ModelAttribute final MarriageRegistration registration)
            throws ParseException {
        final JsonObject response = new JsonObject();
        String[] regcount;
        HashMap<String, Integer> registrationCount;
        JsonArray jsonArray;
        if (registration.getMarriageAct() != null
                && registration.getMarriageAct().getId() != null) {
            regcount = marriageRegistrationReportsService
                    .searchRegistrationActWise(registration, year);
            registrationCount = getCountBymonth(regcount);
            final ArrayList<HashMap<String, Object>> result = new ArrayList<>();
            for (final Map.Entry<Integer, String> monthname : monthMap.entrySet()) {

                final HashMap<String, Object> monthRegMap = new HashMap<>();
                monthRegMap.put(KEY_MONTH, monthname.getValue());
                monthRegMap
                        .put(KEY_REGCOUNT,
                                registrationCount.get(monthname.getValue()) != null ? registrationCount
                                        .get(monthname.getValue()) : 0);
                result.add(monthRegMap);

            }

            jsonArray = (JsonArray) new Gson().toJsonTree(result,
                    new TypeToken<List<HashMap<String, Object>>>() {

                        /**
                 *
                 */
                        private static final long serialVersionUID = -3045535969083515053L;
                    }.getType());

            response.add("data", jsonArray);
        }

        else {
            regcount = marriageRegistrationReportsService
                    .searchRegistrationMrActWise(year);
            registrationCount = getCountByAct(regcount);
            final ArrayList<HashMap<String, Object>> result = new ArrayList<>();
            final List<MarriageAct> actList = marriageActService.getActs();
            for (final MarriageAct actName : actList) {

                final HashMap<String, Object> actRegMap = new HashMap<>();
                actRegMap.put(KEY_ACT, actName.getName());
                actRegMap
                        .put(KEY_REGCOUNT,
                                registrationCount.get(actName.getName()) != null ? registrationCount
                                        .get(actName.getName()) : 0);
                result.add(actRegMap);

            }

            jsonArray = (JsonArray) new Gson().toJsonTree(result,
                    new TypeToken<List<HashMap<String, Object>>>() {

                        /**
                 *
                 */
                        private static final long serialVersionUID = -3419248131719029680L;
                    }.getType());
            response.add("data", jsonArray);
        }
        return response.toString();
    }

    private HashMap<String, Integer> getCountBymonth(final String[] inputs) {

        final HashMap<String, Integer> response = new HashMap<>();

        for (final String input : inputs) {
            final String[] values = input.split(",");
            final Integer month = Integer.valueOf(values[0]);

            for (final Map.Entry<Integer, String> monthname : monthMap.entrySet())
                if (month.equals(monthname.getKey())) {
                    final int existingCount = response.get(monthname) != null ? response
                            .get(monthname) : 0;
                    response.put(monthname.getValue(),
                            existingCount + Integer.valueOf(values[1]));

                }
        }

        return response;
    }

    private HashMap<String, Integer> getCountByAct(final String[] inputs) {

        final HashMap<String, Integer> response = new HashMap<>();

        for (final String input : inputs) {
            final String[] values = input.split(",");
            final String actname = values[0];
            final List<MarriageAct> actList = marriageActService.getActs();
            for (final MarriageAct act : actList)
                if (actname.equals(act.getName())) {
                    final int existingCount = response.get(actname) != null ? response
                            .get(actname) : 0;
                    response.put(actname.toString(),
                            existingCount + Integer.valueOf(values[1]));

                    break;
                }
        }

        return response;
    }

    @RequestMapping(value = "/religionwiseregistration", method = RequestMethod.GET)
    public String showRegionwiseReportForm(final Model model) {
        model.addAttribute("registration", new MarriageRegistration());
        model.addAttribute("religions", religionService.getReligions());
        model.addAttribute("yearlist", getPreviousyears());
        return "religionwise-report";
    }

    public List<Integer> getPreviousyears() {
        final List<Integer> previousyears = new ArrayList<Integer>();
        final int currentyear = Calendar.getInstance().get(Calendar.YEAR);
        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2009);
        int startyear = cal.get(Calendar.YEAR);

        for (int i = startyear; i < currentyear; i++) {
            previousyears.add(startyear + 1);
            startyear++;
        }
        return previousyears;
    }

    @RequestMapping(value = "/religionwiseregistration", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String showRegionwiseReportresult(
            @RequestParam("year") final int year, final Model model,
            @ModelAttribute final MarriageRegistration registration)
            throws ParseException {
        final List<MarriageRegistration> searchResultList = marriageRegistrationReportsService
                .searchRegistrationByreligion(registration, year);
        return new StringBuilder("{ \"data\":")
                .append(toJSON(searchResultList, MarriageRegistration.class,
                        MarriageRegistrationJsonAdaptor.class)).append("}")
                .toString();
    }

    @RequestMapping(value = "/act-wise/view/{year}/{MarriageAct}", method = RequestMethod.GET)
    public String viewActWiseDetails(@PathVariable final int year,
            @PathVariable final String MarriageAct, final Model model)
            throws IOException, ParseException {
        final List<MarriageRegistration> marriageRegistrations = marriageRegistrationReportsService
                .getActwiseDetails(year, MarriageAct);
        model.addAttribute("marriageRegistrations", marriageRegistrations);
        return "marriage-actwise-view";
    }

    @RequestMapping(value = "/act-wise/view/{year}/{month}/{actid}", method = RequestMethod.GET)
    public String viewActWiseDetails(@PathVariable final int year,
            @PathVariable final String month, @PathVariable final Long actid,
            final Model model) throws IOException, ParseException {
        final Date date = new SimpleDateFormat("MMM").parse(month);
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        final int months = cal.get(Calendar.MONTH) + 1;
        final List<MarriageRegistration> marriageRegistrations = marriageRegistrationReportsService
                .getmonthWiseActDetails(year, months, actid);
        model.addAttribute("marriageRegistrations", marriageRegistrations);
        return "marriage-actwise-view";
    }

}
