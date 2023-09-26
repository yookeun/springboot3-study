-- STUDY.`member` definition
USE STUDY;
DROP TABLE `MEMBER`;
DROP TABLE `MEMBER_AUTHORITY`;
DROP TABLE `ITEM`;
DROP TABLE `ORDER`;


CREATE TABLE `MEMBER` (
                          `MEMBER_ID` bigint NOT NULL AUTO_INCREMENT,
                          `USER_ID` varchar(20) NOT NULL,
                          `PASSWORD` varchar(100) NOT NULL,
                          `GENDER`varchar(20) NOT NULL,
                          `NAME` varchar(20)  NOT NULL,
                          `CREATE_DATE` datetime DEFAULT NULL,
                          `UPDATE_DATE` datetime DEFAULT NULL,
                          PRIMARY KEY (`MEMBER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- STUDY.member_authority definition

CREATE TABLE `MEMBER_AUTHORITY` (
                                    `MEMBER_AUTHORITY_ID` bigint NOT NULL AUTO_INCREMENT,
                                    `MEMBER_ID` bigint DEFAULT NULL,
                                    `AUTHORITY` varchar(20) DEFAULT NULL,
                                    `CREATE_DATE` datetime DEFAULT NULL,
                                    `UPDATE_DATE` datetime DEFAULT NULL,
                                    PRIMARY KEY (`MEMBER_AUTHORITY_ID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- STUDY.item definition

CREATE TABLE `ITEM` (
                        `ITEM_ID` bigint NOT NULL AUTO_INCREMENT,
                        `PRICE` bigint DEFAULT NULL,
                        `ITEM_NAME` varchar(255) DEFAULT NULL,
                        `ITEM_TYPE` varchar(20) DEFAULT NULL,
                        `CREATE_DATE` datetime DEFAULT NULL,
                        `UPDATE_DATE` datetime DEFAULT NULL,
                        PRIMARY KEY (`ITEM_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE `ORDER` (
                         `ORDER_ID` bigint NOT NULL AUTO_INCREMENT,
                         `MEMBER_ID` bigint DEFAULT NULL,
                         `ITEM_ID` bigint DEFAULT NULL,
                         `ORDER_COUNT` int DEFAULT NULL,
                         `ORDER_STATUS` varchar(20) DEFAULT NULL,
                         `CREATE_DATE` datetime DEFAULT NULL,
                         `UPDATE_DATE` datetime DEFAULT NULL,
                         PRIMARY KEY (`ORDER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
