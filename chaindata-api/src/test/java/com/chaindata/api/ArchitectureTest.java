package com.chaindata.api;

import static com.tngtech.archunit.base.DescribedPredicate.not;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields;
import static com.tngtech.archunit.library.GeneralCodingRules.BE_ANNOTATED_WITH_AN_INJECTION_ANNOTATION;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(
        packages = "com.chaindata.api",
        importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    @ArchTest
    static final ArchRule domainMustNotDependOnInfrastructure = noClasses()
            .that()
            .resideInAPackage("..domain..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("..infrastructure..")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule domainMustNotDependOnApplication = noClasses()
            .that()
            .resideInAPackage("..domain..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("..application..")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule domainMustNotImportSpringExceptAllowed = noClasses()
            .that()
            .resideInAPackage("..domain..")
            .should()
            .dependOnClassesThat(
                    resideInAnyPackage("org.springframework..")
                            .and(not(resideInAnyPackage(
                                    "org.springframework.stereotype..",
                                    "org.springframework.transaction.annotation.."))))
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule domainMustNotImportJakartaPersistence = noClasses()
            .that()
            .resideInAPackage("..domain..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("jakarta.persistence..")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule domainMustNotImportSpringDataRelational = noClasses()
            .that()
            .resideInAPackage("..domain..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("org.springframework.data.relational..")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule applicationMustNotDependOnInfrastructure = noClasses()
            .that()
            .resideInAPackage("..application..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("..infrastructure..")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule infrastructureMustNotDependOnApplication = noClasses()
            .that()
            .resideInAPackage("..infrastructure..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("..application..")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule noFieldInjection = noFields()
            .should(BE_ANNOTATED_WITH_AN_INJECTION_ANNOTATION)
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule noGenericExceptions = NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;

    @ArchTest
    static final ArchRule domainModelClassesShouldBeRecords = classes()
            .that()
            .resideInAPackage("..domain.model..")
            .and()
            .areNotEnums()
            .and()
            .areNotInterfaces()
            .and()
            .areNotMemberClasses()
            .should()
            .beRecords()
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule domainPortsMustBeInterfaces = classes()
            .that()
            .resideInAPackage("..domain.port..")
            .should()
            .beInterfaces()
            .allowEmptyShould(true);
}
