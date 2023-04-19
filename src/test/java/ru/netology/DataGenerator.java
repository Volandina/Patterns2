package ru.netology;

import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.util.Locale;

import static io.restassured.RestAssured.given;

public class DataGenerator {
    private static final Faker faker = new Faker(new Locale("en"));
    private static final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    private DataGenerator() {
    }

    private static void sendRequest(UserInfo user) {

        given()
                .spec(requestSpec)
                .body(new UserInfo(
                        user.getLogin(),
                        user.getPassword(),
                        user.getStatus()
                ))
                .when()
                .post("/api/system/users")
                .then()
                .statusCode(200);
    }

    public static class Registration {
        private Registration() {
        }

        public static String generateLogin() {
            return faker.name().username();
        }

        public static String generatePassword() {
            return faker.internet().password();
        }

        public static UserInfo generateValidUser() {
            UserInfo userInfo = new UserInfo(generateLogin(), generatePassword(), "active");
            sendRequest(userInfo);
            return userInfo;
        }

        public static UserInfo generateBlockedUser() {
            UserInfo userInfo = new UserInfo(generateLogin(), generatePassword(), "blocked");
            sendRequest(userInfo);
            return userInfo;
        }

        public static UserInfo generateWrongPasswordUser(String status) {
            String login = generateLogin();
            sendRequest(new UserInfo(login, generatePassword(), status));
            return new UserInfo(login, generatePassword(), status);
        }

        public static UserInfo generateWrongLoginUser(String status) {
            String password = generatePassword();
            sendRequest(new UserInfo(generateLogin(), password, status));
            return new UserInfo(generateLogin(), password, status);
        }
    }
}
