# lifecycle-consumption-calculator

## Objective
### Showing people their approximate value of proper annual consumption based on Life-Cycle Hypothesis (Modigliani & Brumberg 1954)

## Setting up the project
### 1. Setup Using Spring Initializr
<img width="1920" height="1080" alt="LCCC_SpringInitializr" src="https://github.com/user-attachments/assets/5fd11368-0b2b-4c84-9e0d-ffdba749d0ec" />

For both web application using Tomcat server and an individual program builds, JAR is selected.


|Dependencies|
|------------|
|Spring Web  |
|Spring JPA  |
|MySQL       |
|Thymeleaf   |
|Lombok      |
|Spring Security|



### 2. Project Directory
``` Directory
app
|- src
|    |- main
|       |- java
|       |   |- com.lifecycleincome.app
|       |               |- controller
|       |               |- AppApplication.java
|       |- resources
|             |- static
|             |- templates
|             |- applications.yaml
|- pom.xml
```


### 3. Making Controllers and View for test
Created MainController.java under 'controller' foler, mapped with the main address (/) for test purpose:
``` Java
@Controller
public class MainController {
  @GetMapping("/") 
  public String home() {
    return "index";
  }
}
```
After adding an index.html under [/resources/templates], the html appeared well on http://localhost:8080.



### 4. Making Temporary Service using the controller and more advanced view
Added the following method to MainController
``` Java
@PostMapping("/calculate")
    public String calculate(
            @RequestParam("currentAge") int currentAge,
            @RequestParam("retirementAge") int retirementAge,
            @RequestParam("currentAssets") double currentAssets,
            @RequestParam("annualIncome") double annualIncome,
            @RequestParam("country") String country,
            Model model) {

        // 1. Temporal Setting of life expectancy per country
        int lifeExpectancy = switch (country) {
            case "Canada" -> 83;
            case "USA" -> 84;
            case "Mexico" -> 85;
            default -> 80; 
        };

        // 2. Calculation of remaining working years and remaining life years
        int workingYears = retirementAge - currentAge; 
        int remainingLifeYears = lifeExpectancy - currentAge;

        // 3. Temporal Exception Handling
        if (workingYears < 0 || remainingLifeYears <= 0) {
            model.addAttribute("error", "Retirement age or current age is not valid.");
            return "index";
        }

        // 4. Calculation of total wealth
        double totalWealth = currentAssets + (annualIncome * workingYears);

        // 5. Calculation of annual consumption
        double annualConsumption = totalWealth / remainingLifeYears;

        // 6. Put the results in Model to transfer them to the view
        model.addAttribute("result", Math.round(annualConsumption)); 
        model.addAttribute("workingYears", workingYears);
        model.addAttribute("remainingLifeYears", remainingLifeYears);
        
        // 7. Put original values back into the Model
        model.addAttribute("currentAge", currentAge);
        model.addAttribute("retirementAge", retirementAge);
        model.addAttribute("currentAssets", currentAssets);
        model.addAttribute("annualIncome", annualIncome);
        model.addAttribute("country", country);

        return "index";
    }
```
And with basic html, the result was following:
<img width="859" height="966" alt="LCCC_InitialWebPage" src="https://github.com/user-attachments/assets/a476110d-7dc4-4ad6-9f0d-d19cd400da1a" />


### 5. Running Docker with MySql
```Bash
docker run -d \
  --name mysql-portfolio \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=my_password \
  -e MYSQL_DATABASE=lifecycle_db \
  -v ~/docker/mysql/data:/var/lib/mysql \
  mysql:8.0
```
Named the container as  mysql-portfolio, and mapped my PC's 3306 port with container's 3306 port.
Lifecycle_db is the initial database we use and -v specifies where our volume is (permanent storage)

On "application.yaml", edit password under [spring => datasource] same as the password set during starting Docker.

#### Problem occured: I deleted the container for password change and tried to run it again, but I got 
``` Terminal
org.hibernate.exception.AuthException: Unable to obtain isolated JDBC connection [Access denied for user 'root'@'172.17.0.1' (using password: YES)] [n/a]
```
This error. 
Since my volume had got the old password, application.yaml's password and the new container's password did not match.

#### Solution: Because I've got no other data in mysql, Removed all data of mysql using 
``` Bash
sudo rm -rf ~/docker/mysql/data
```
Now I can re-run the docker and JPA will be able to handle SQL queries between future repositories and the container.


### 6. Writing a test @Entity class and see if JPA works correctly
application.yaml hibernate configuration:
``` yaml
jpa:
    show-sql: true
    database-platform: org.hibernate.dialect.MySQLDialect
    hibernate:
      ddl-auto: update # this will enabled JPA to create and update the table based on entity
    properties:
      hibernate:
        format_sql: true # print nicely
```
Test @Entity class:
``` Java
package com.lifecycleincome.app.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_data")
@Getter @Setter
public class UserData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer currentAge;
    private Double annualIncome;
    private Double calculatedConsumption;
    private String country;
    private Double currentAssets;
    private Integer retirementAge;
}
```
Turning on the server => Hibernate reads the code => Creates "consumption_plans" table in Docker MySQL 
Which I could check bia IntelliJ IDEA:

<img width="424" height="453" alt="LCCC_Database_JPA" src="https://github.com/user-attachments/assets/476eaa13-d697-496f-8ca2-0c7068ea2e78" />

## Building up the project
### 1. Directory Setup
``` Directory
app
|- src
|    |- main
|       |- java
|       |   |- com.lifecycleincome.app
|       |               |- config
|       |               |- controller
|       |               |- entity
|       |               |- repository
|       |               |- service
|       |               |- AppApplication.java
|       |- resources
|             |- static
|             |- templates
|             |      |- index.html, and so on here
|             |- applications.yaml
|- pom.xml
```
Implementation TODOs:
|config|controller|entity|repository|service|
|------|----------|------|----------|-------|
|Principal classes|MainController (homepage)|CalculationRecord|CalculationRecordRepository|CalculationService|
|Security classes|UserController (account handling)|SiteUser|UserRepository|UserService|

### 2. User creation, Sign-up implementation
Since the project already has a temporary calculation logic in MainController.java, I will create a sign-up classes first.
  |Class|Features|
  |:----|:-------|
  |SecurityConfig.java|```@Bean``` methods that Spring will use anytime the user signs up or logs in, ```BCryptPasswordEncoder``` to hash the password, and ```SecurityFilterChain``` to allow/deny access|
  |SiteUser.java|Generative ID, username, and password that is accesible by JPA to create a table "site_users" which indicates currently active users on the server|
  |UserRepository.java|interface extending ```JpaRepository<SiteUser, Long>``` with the method of ```Optional<SiteUser> findByUsername(String username)``` to search users in the database without manually checking null|
  |UserService.java|Logic of signing up. Initial method of ```create(String username, String password)``` with ```BCryptPasswordEncoder``` to encode the password, lombok+SiteUser to set password, and return SiteUser by ```UserRepository.save(user)```(UserRepository extends JpaRepository so it is possible)|
  |UserController.java|Its object is created at the runtime with UserService parameter using ```@RequiredArgsContructor```. If html wants to post at /signup, this object handle it using ```singup(@RequestParam("username") String, @RequestParam("password") String)```which takes username and password as parameters and tries to create a SiteUser with the username and password.|

### 3. Log-in implementation

  |Class|Features|
  |:----|:-------|
  |PrincipalDetails.java|Data-transfer object which implements ```UserDetails``` to fulfil the requirement of Spring Security (getting password, username, account, credential, authority role information)|
  |PrincipalDetailsService.java|Data-transfer object which implements ```UserDetailsService``` to fulfil the requirement of Spring Security when logging in. It governs the flow of [Database => UserRepository => PrincipalDetails => Spring Security].|
  |SecurityConfig.java-edit|added .formLogin() with ```loginPage``` (defines what path to use for login), ```loginProcessingUrl``` (login.html's address), ```defaultSuccessUrl```, and ```failureUrl```|
  |login.html|Login page with form of POST method at action /user/login|
  |LoginController.java|Just ```@GetMapping("/login")``` method returning "login" to specify ```login.html``` under templates foler|
  |UserController.java-edit| added @GetMapping("/signup") to map it to ```signup.html``` which has a similar design to ```login.html```. Also, after creating the SiteUser for successful signup, ```return "redirect:/login"```, requesting login or direct to error page for unsuccessful singup.|

#### Check List
|Condition|Satisfied|
|:--------|:--------|
|Login page pops up before main(/)|Yes|
|Login by unknown username or wrong password does not work|Yes|
|Login by known username and valid password works|Yes|
|Signup by existing username does not work|Yes|
|Signup by non-existing username and login with it works|Yes|

### 4. Calculation Services
The basic structure is Many (calculation records) to One (SiteUser)
|Class|Features|
|:----|:-------|
|MainController.java-edit|1. Now it will get Model(information container used by Spring for html), and Principal(ID card of the current user which is used by Spring), then send the name at the principal to the model, and send the model to index.html. 2. When the client posts calculation, it will find the user, retrieve ```CalculationRecord``` using ```CalculationService``` below, add all the returned attributes from the record to the model, and send it to index.html.|
|CalculationRecord.java|JPA Accessible ```@Entity``` similar to SiteUser but is recorded with ```LocalDateTime``` and is linked to SiteUser using ```@ManyToOne @JoinColumn(name = "site_user_id")```|
|CalculationRecordRepository.java|Interface extending ```JpaRepository<CalculationRecord, Long>``` so that all of calculation records of a user can be retrieved from database which is implemented by ```List<CalculationRecord> findBySiteUserOrderByCreatedAtDesc(SiteUser siteUser);```|
|CalculationService.java|It will calculate and return the record to the ```MainController```, and save the record. It also provides history of records to ```MainController``` using ```CalculationRecordRepository```.|

### 5. Deleting and Updating users
For now users can Create and Read their account and account history, but cannot Delete and Update their account based on CRUD principle. For that reason, I will update the codes a little bit to support deletion of the user accout and updating password.

|Classes Modified|Description|
|:---------------|:----------|
|SiteUser|Since calculation records are many to one SiteUser, if the user is deleted, one (SiteUser) to many calculation records deletion should also happen cascadingly.|
|UserService|1. Added ```delete(String username)``` so that it can find a user by the username and delete the user from the database using ```UserRepository``` which has a delete function since it extends ```JpaRepository```. 2. Added ```update(String username, String password)``` which finds the user using ```UserRepository```, encodes the password using ```BCryptPasswordEncoder```, sets password, and saves the user using ```UserRepository```.|
|UserController| Post mapping for ```/delete```. It will call ```UserService``` to delete the user, invalidate the current session and log out the user. 2. Added GetMapping for ```/settings```, which retrieves username from the principal and sending it to the ```settings.html```. Added PostMapping for ```/settings``` which tells ```UserService``` to update the user with the given password and sending the user to the login page if success or the main page if unsuccess.|
|index.html|Delete button with post method for ```/delete``` and the Settings button with get method for ```/settings```|
|settings.html|Similar to login page but without username input(UserController passes the username automatically as settings page is only accessible when logged in).|

