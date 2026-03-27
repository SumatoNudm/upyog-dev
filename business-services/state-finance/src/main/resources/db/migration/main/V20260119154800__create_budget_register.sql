CREATE TABLE budget_register(
    id BIGSERIAL PRIMARY KEY,

    budgetregisterid BIGINT NOT NULL,

    tenantid VARCHAR(50) NOT NULL,

    budgetregisternumber VARCHAR(100) NOT NULL,
    budgetregistername VARCHAR(100) NOT NULL,

    startingdate TIMESTAMP WITHOUT TIME ZONE,
    endingdate TIMESTAMP WITHOUT TIME ZONE,

--    endingdate TIMESTAMP WITHOUT TIME ZONE,

    currentfy VARCHAR(50) NOT NULL,
    nextfy VARCHAR(50) NOT NULL


)