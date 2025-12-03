package com.buildingenergy.substation_manager.aspect;

import com.buildingenergy.substation_manager.company.model.Company;
import com.buildingenergy.substation_manager.meter.model.Meter;
import com.buildingenergy.substation_manager.reading.model.ReadingHistory;
import com.buildingenergy.substation_manager.report.service.ExcelExportService;
import com.buildingenergy.substation_manager.user.model.User;
import com.buildingenergy.substation_manager.user.model.UserRole;
import com.buildingenergy.substation_manager.web.dto.MeterReadingRequest;
import com.buildingenergy.substation_manager.web.dto.MeterRequest;
import com.buildingenergy.substation_manager.web.dto.ReadingListWrapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.security.authentication.DisabledException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.UUID;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @AfterReturning(
            pointcut = "execution(* com.buildingenergy.substation_manager.user.service.UserService.register(..))",
            returning = "result"
    )
    public void afterUserRegistration(Object result) {
        User user = (User) result;

        log.info("User %s has registered successfully".formatted(user.getUsername()));
    }

    @AfterThrowing(
            pointcut = "execution(* com.buildingenergy.substation_manager.user.service.UserService.loadUserByUsername(..))",
            throwing = "ex"
    )
    public void userInactiveLogin(JoinPoint jp, Exception ex) {
        if (ex instanceof DisabledException) {
            String username = (String) jp.getArgs()[0];
            log.warn("Authentication rejected for inactive user: %s".formatted(username));
        }
    }

    @After("execution(* com.buildingenergy.substation_manager.user.service.UserService.updateProfile(..))")
    public void afterProfileUpdate(JoinPoint jp) {
        User user = (User) jp.getArgs()[0];

        log.info("User with id: [%s] updated his profile.".formatted(user.getId()));
    }

    @AfterReturning(
            pointcut = "execution(* com.buildingenergy.substation_manager.user.service.UserService.changeStatus(..))",
            returning = "admin"
    )
    public void afterStatusChange(JoinPoint jp, User admin) {
        User targetUser = (User) jp.getArgs()[0];

        log.info("Admin [%s] changed account status of user [%s] from [%s] to [%s].".formatted(
                admin.getUsername(),
                targetUser.getUsername(),
                targetUser.isActive() ? "Inactive" : "Active",
                targetUser.isActive() ? "Active" : "Inactive"
        ));
    }

    @AfterReturning(
            pointcut = "execution(* com.buildingenergy.substation_manager.user.service.UserService.updateRole(..))",
            returning = "updatedUser"
    )
    public void afterRoleChange(JoinPoint jp, User updatedUser) {
        User actingUser = (User) jp.getArgs()[1];

        String oldRole = updatedUser.getRole() == UserRole.ADMIN
                ? UserRole.USER.getDisplayName()
                : UserRole.ADMIN.getDisplayName();

        String newRole = updatedUser.getRole().getDisplayName();

        log.info("Admin [%s] changed role of user [%s] from [%s] to [%s]".formatted(
                actingUser.getUsername(),
                updatedUser.getUsername(),
                oldRole,
                newRole
        ));
    }

    @After("execution(* com.buildingenergy.substation_manager.company.service.CompanyService.addCompanyForFloor(..))")
    public void afterAddCompanyForFloor(JoinPoint jp) {
        String companyName = (String) jp.getArgs()[0];
        User user = (User) jp.getArgs()[2];

        log.info("Company [%s] added for user [%s]".formatted(companyName, user.getUsername()));
    }

    @AfterReturning(
            pointcut = "execution(* com.buildingenergy.substation_manager.company.service.CompanyService.deleteCompany(..))",
            returning = "deletedCompany"
    )
    public void afterDeleteCompany(Company deletedCompany) {
        String companyName = deletedCompany.getName();
        User user = deletedCompany.getUser();

        log.warn("Company [%s] deleted for user: [%s] with id: [%s]".formatted(companyName, user.getUsername(), user.getId()));
    }

    @After("execution(* com.buildingenergy.substation_manager.meter.service.MeterService.createMeter(..))")
    public void afterCreateMeter(JoinPoint jp) {
        String meterName = ((MeterRequest) jp.getArgs()[0]).getMeterName();
        User user = (User) jp.getArgs()[3];

        log.info("Added meter [%s] for user [%s]".formatted(meterName, user.getUsername()));
    }

    @After("execution(* com.buildingenergy.substation_manager.meter.service.MeterService.swapMeterReadings(..))")
    public void afterSwap(JoinPoint jp) {
        List<Meter> meters = (List<Meter>) jp.getArgs()[0];

        if (meters == null || meters.isEmpty()) {
            log.warn("swapMeterReadings() called with no meters");
            return;
        }

        log.info("User [%s] swapped [%d] meter readings at [%s]".formatted(
                meters.get(0).getUser().getUsername(),
                meters.size(),
                LocalDateTime.now()
        ));
    }

    @AfterReturning(
            pointcut = "execution(* com.buildingenergy.substation_manager.meter.service.MeterService.updateMeterReadings(..))",
            returning = "meters"
    )
    public void afterUpdateMeterReadings(JoinPoint jp, List<Meter> meters) {
        List<MeterReadingRequest> readings = (List<MeterReadingRequest>) jp.getArgs()[0];
        User user = (User) jp.getArgs()[1];

        if (meters == null || meters.isEmpty()) {
            log.warn("updateMeterReadings() called but no meters were found.");
            return;
        }

        if (readings == null || readings.size() != meters.size()) {
            log.error("Mismatch between meters [%d] and readings [%d]. Update aborted for user [%s].".formatted(
                    meters.size(),
                    readings == null ? 0 : readings.size(),
                    user.getUsername()
            ));

            return;
        }

        log.info("User [%s] updated [%d] meters readings.".formatted(user.getUsername(), meters.size()));
    }

    @After("execution(* com.buildingenergy.substation_manager.meter.service.MeterHistoryService.deleteMeterByIdAndMonth(..))")
    public void afterDeleteMeterByIdAndMonth(JoinPoint jp) {
        UUID meterId = (UUID) jp.getArgs()[0];
        int month = (int) jp.getArgs()[1];

        log.info("Deleted reading history for meter with id: [%s] for month [%s]".formatted(meterId, Month.of(month)));
    }

    @After("execution(* com.buildingenergy.substation_manager.reading.service.ReadingService.updateAllReadings(..))")
    public void afterUpdateAllReadings(JoinPoint jp) {
        ReadingListWrapper wrapper = (ReadingListWrapper) jp.getArgs()[0];
        UUID userId = (UUID) jp.getArgs()[1];

        if (wrapper.getReadings() == null || wrapper.getReadings().isEmpty()) {
            log.warn("updateAllReadings() called but no companies were provided.");
            return;
        }

        log.info("User with id: [%s] updated [%d] company readings at: [%s]".formatted(
                userId,
                wrapper.getReadings().size(),
                LocalDateTime.now()
        ));
    }

    @AfterReturning(
            pointcut = "execution(* com.buildingenergy.substation_manager.reading.service.ReadingService.swapAllReadingsForFloor(..))",
            returning = "companies"
    )
    public void afterSwapAllReadingsForFloor(JoinPoint jp, List<Company> companies) {
        User user = (User) jp.getArgs()[1];

        if (companies == null || companies.isEmpty()) {
            log.warn("swapAllReadingsForFloor() called by user [%s] but no companies were found.".formatted(user.getUsername()));
            return;
        }

        log.info("User [%s] swapped [%d] company readings at: [%s]".formatted(
                user.getUsername(),
                companies.size(),
                LocalDateTime.now()
        ));
    }

    @AfterReturning(
            pointcut = "execution(* com.buildingenergy.substation_manager.reading.service.ReadingHistoryService.deleteCompanyByIdAndMonth(..))",
            returning = "reading"
    )
    public void afterDeleteCompanyByIdAndMonth(JoinPoint jp, ReadingHistory reading) {
        int month = (int) jp.getArgs()[1];

        log.info("Deleted reading history for company: [%s] for month [%s]".formatted(
                reading.getCompanyNameSnapshot(),
                Month.of(month)
        ));
    }

    @AfterReturning(
            pointcut = "execution(* com.buildingenergy.substation_manager.report.service.ExcelExportService.exportReadingHistory(..))",
            returning = "result"
    )
    public void afterExportCompanyHistory(ExcelExportService.ExportResult result) {
        log.info("Exporting company history for month [%s] for user with id: [%s]".formatted(
                result.monthWord(),
                result.userId()
        ));
    }

    @AfterReturning(
            pointcut = "execution(* com.buildingenergy.substation_manager.report.service.ExcelExportService.exportMeterHistory(..))",
            returning = "result"
    )
    public void afterExportMeterHistory(ExcelExportService.ExportResult result) {
        log.info("Exporting meter history for month [%s] for user with id: [%s]".formatted(
                result.monthWord(),
                result.userId()
        ));
    }

}
