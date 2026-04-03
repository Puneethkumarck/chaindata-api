package com.chaindata.api;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields;

@AnalyzeClasses(
        packages = "com.chaindata.api",
        importOptions = ImportOption.DoNotIncludeTests.class
)
class ArchitectureTest {

    private static final String DOMAIN = "..domain..";
    private static final String DOMAIN_MODEL = "..domain.model..";
    private static final String DOMAIN_PORT = "..domain.port..";
    private static final String INFRASTRUCTURE = "..infrastructure..";
    private static final String APPLICATION = "..application..";

    // -----------------------------------------------------------------------
    // Rule 1: domain must NOT depend on infrastructure
    // -----------------------------------------------------------------------

    @ArchTest
    static final ArchRule domain_must_not_depend_on_infrastructure =
            noClasses()
                    .that().resideInAPackage(DOMAIN)
                    .should().dependOnClassesThat().resideInAPackage(INFRASTRUCTURE)
                    .allowEmptyShould(true)
                    .because("Domain layer must be independent of infrastructure (hexagonal architecture)");

    // -----------------------------------------------------------------------
    // Rule 2: domain must NOT depend on application
    // -----------------------------------------------------------------------

    @ArchTest
    static final ArchRule domain_must_not_depend_on_application =
            noClasses()
                    .that().resideInAPackage(DOMAIN)
                    .should().dependOnClassesThat().resideInAPackage(APPLICATION)
                    .allowEmptyShould(true)
                    .because("Domain layer must be independent of application layer (hexagonal architecture)");

    // -----------------------------------------------------------------------
    // Rule 3: domain must NOT import Spring except @Service, @Transactional
    //         Reactive adaptation: reactor.core.publisher (Mono/Flux) is allowed
    // -----------------------------------------------------------------------

    @ArchTest
    static final ArchRule domain_must_not_import_spring_except_allowed =
            noClasses()
                    .that().resideInAPackage(DOMAIN)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(
                            "org.springframework.web..",
                            "org.springframework.data..",
                            "org.springframework.kafka..",
                            "org.springframework.boot..",
                            "org.springframework.context..",
                            "org.springframework.http..",
                            "org.springframework.cloud.."
                    )
                    .allowEmptyShould(true)
                    .because("Domain must not import Spring framework classes — "
                            + "only @Service, @Transactional, and reactor.core.publisher are allowed");

    // -----------------------------------------------------------------------
    // Rule 4: domain must NOT import jakarta.persistence (no JPA)
    // -----------------------------------------------------------------------

    @ArchTest
    static final ArchRule domain_must_not_import_jpa =
            noClasses()
                    .that().resideInAPackage(DOMAIN)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("jakarta.persistence..", "javax.persistence..")
                    .allowEmptyShould(true)
                    .because("Domain model must be persistence-ignorant — "
                            + "JPA annotations belong in infrastructure");

    // -----------------------------------------------------------------------
    // Rule 5: domain must NOT import spring-data-relational (R2DBC annotations)
    // -----------------------------------------------------------------------

    @ArchTest
    static final ArchRule domain_must_not_import_spring_data_relational =
            noClasses()
                    .that().resideInAPackage(DOMAIN)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("org.springframework.data.relational..")
                    .allowEmptyShould(true)
                    .because("R2DBC @Table/@Id annotations belong in infrastructure.persistence — "
                            + "domain must not import spring-data-relational");

    // -----------------------------------------------------------------------
    // Rule 6: application must NOT depend on infrastructure
    // -----------------------------------------------------------------------

    @ArchTest
    static final ArchRule application_must_not_depend_on_infrastructure =
            noClasses()
                    .that().resideInAPackage(APPLICATION)
                    .should().dependOnClassesThat().resideInAPackage(INFRASTRUCTURE)
                    .allowEmptyShould(true)
                    .because("Application layer must depend on domain ports, not infrastructure directly");

    // -----------------------------------------------------------------------
    // Rule 7: infrastructure must NOT depend on application
    // -----------------------------------------------------------------------

    @ArchTest
    static final ArchRule infrastructure_must_not_depend_on_application =
            noClasses()
                    .that().resideInAPackage(INFRASTRUCTURE)
                    .should().dependOnClassesThat().resideInAPackage(APPLICATION)
                    .allowEmptyShould(true)
                    .because("Infrastructure adapters must not depend on application layer — "
                            + "they implement domain ports only");

    // -----------------------------------------------------------------------
    // Rule 8: No field injection (@Autowired on fields)
    // -----------------------------------------------------------------------

    @ArchTest
    static final ArchRule no_field_injection =
            noFields()
                    .should().beAnnotatedWith("org.springframework.beans.factory.annotation.Autowired")
                    .allowEmptyShould(true)
                    .because("Use constructor injection via @RequiredArgsConstructor — "
                            + "never @Autowired on fields");

    // -----------------------------------------------------------------------
    // Rule 9: No generic exceptions (must use domain-specific CDA-XXXX)
    // -----------------------------------------------------------------------

    @ArchTest
    static final ArchRule no_generic_exceptions =
            noClasses()
                    .that().resideInAnyPackage(DOMAIN, APPLICATION, INFRASTRUCTURE)
                    .should().dependOnClassesThat()
                    .haveFullyQualifiedName("java.lang.RuntimeException")
                    .orShould().dependOnClassesThat()
                    .haveFullyQualifiedName("java.lang.Exception")
                    .allowEmptyShould(true)
                    .because("Use domain-specific exceptions with CDA-XXXX error codes — "
                            + "never throw generic RuntimeException or Exception");

    // -----------------------------------------------------------------------
    // Rule: domain model classes should be records
    // -----------------------------------------------------------------------

    @ArchTest
    static final ArchRule domain_model_classes_should_be_records =
            classes()
                    .that().resideInAPackage(DOMAIN_MODEL)
                    .and().areNotEnums()
                    .and().areNotInterfaces()
                    .and().areNotMemberClasses()
                    .should().beRecords()
                    .allowEmptyShould(true)
                    .because("Domain model classes must be Java records for immutability");

    // -----------------------------------------------------------------------
    // Rule: domain ports must be interfaces
    // -----------------------------------------------------------------------

    @ArchTest
    static final ArchRule domain_ports_must_be_interfaces =
            classes()
                    .that().resideInAPackage(DOMAIN_PORT)
                    .should().beInterfaces()
                    .allowEmptyShould(true)
                    .because("Domain ports define contracts and must be interfaces "
                            + "that infrastructure adapters implement");
}
