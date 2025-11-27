package com.buildingenergy.substation_manager.reading;

import com.buildingenergy.substation_manager.company.model.Company;
import com.buildingenergy.substation_manager.company.repository.CompanyRepository;
import com.buildingenergy.substation_manager.exception.CompanyNotFound;
import com.buildingenergy.substation_manager.floor.model.Floor;
import com.buildingenergy.substation_manager.reading.model.Reading;
import com.buildingenergy.substation_manager.reading.repository.ReadingRepository;
import com.buildingenergy.substation_manager.reading.service.ReadingService;
import com.buildingenergy.substation_manager.user.model.User;
import com.buildingenergy.substation_manager.web.dto.ReadingListWrapper;
import com.buildingenergy.substation_manager.web.dto.ReadingRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReadingServiceUTest {

    @Mock
    private ReadingRepository readingRepository;
    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private ReadingService readingService;

    @Test
    void givenCompany_whenCreateDefaultReading_thenSaveNewReading() {
        Company company = new Company();

        readingService.createDefaultReading(company);

        verify(readingRepository).save(any(Reading.class));
    }

    @Test
    void givenExistingCompanyWithoutReading_whenUpdateReading_thenCreateNewReading() {
        UUID companyId = UUID.randomUUID();
        Company company = new Company();
        company.setId(companyId);

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(readingRepository.findByCompany(company)).thenReturn(Optional.empty());

        ReadingRequest req = buildRequest(companyId);

        readingService.updateReadingForCompany(req);

        verify(readingRepository).save(any(Reading.class));
    }

    @Test
    void givenMissingCompanyId_whenUpdateReading_thenThrowCompanyNotFoundException() {
        UUID id = UUID.randomUUID();
        ReadingRequest req = buildRequest(id);

        when(companyRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(CompanyNotFound.class, () -> readingService.updateReadingForCompany(req));
    }

    @Test
    void givenExistingReadingForCompany_whenFindByCompany_thenReturnReading() {
        Company company = new Company();
        Reading r = new Reading();

        when(readingRepository.findByCompany(company)).thenReturn(Optional.of(r));

        assertEquals(r, readingService.findByCompany(company));
    }

    @Test
    void givenNoReadingForCompany_whenFindByCompany_thenReturnNull() {
        Company company = new Company();

        when(readingRepository.findByCompany(company)).thenReturn(Optional.empty());

        assertNull(readingService.findByCompany(company));
    }

    @Test
    void givenFloorWithCompaniesAndExistingReadings_whenSwapAllReadings_thenMoveNewToOldAndResetNewToZero() {
        Floor floor = new Floor();
        User user = new User();

        Company c = new Company();
        c.setUser(user);

        Reading r = new Reading();
        r.setNewReadingM1(BigDecimal.TEN);
        r.setNewReadingM2(BigDecimal.ONE);

        when(companyRepository.findAllByFloorAndUser(floor, user)).thenReturn(List.of(c));
        when(readingRepository.findByCompany(c)).thenReturn(Optional.of(r));

        readingService.swapAllReadingsForFloor(floor, user);

        verify(readingRepository).save(r);
        assertEquals(BigDecimal.TEN, r.getOldReadingM1());
        assertEquals(BigDecimal.ONE, r.getOldReadingM2());
        assertEquals(BigDecimal.ZERO, r.getNewReadingM1());
        assertEquals(BigDecimal.ZERO, r.getNewReadingM2());
    }

    @Test
    void givenExistingReading_whenGetReadingRequests_thenReturnMappedReadingRequest() {
        Company c = new Company();
        c.setId(UUID.randomUUID());

        Reading r = new Reading();
        r.setOffice("Office1");

        when(readingRepository.findByCompany(c)).thenReturn(Optional.of(r));

        List<ReadingRequest> result = readingService.getReadingRequests(List.of(c));

        assertEquals(1, result.size());
        assertEquals("Office1", result.get(0).getOffice());
    }

    @Test
    void givenMissingReading_whenGetReadingRequests_thenReturnRequestWithZeroDefaults() {
        Company c = new Company();
        c.setId(UUID.randomUUID());

        when(readingRepository.findByCompany(c)).thenReturn(Optional.empty());

        List<ReadingRequest> result = readingService.getReadingRequests(List.of(c));

        assertEquals(BigDecimal.ZERO, result.get(0).getOldReadingM1());
    }

    @Test
    void givenCompanyList_whenGetWrapperForCompanies_thenReturnWrapperWithMappedRequests() {
        Company c = new Company();
        c.setId(UUID.randomUUID());

        ReadingListWrapper wrapper = readingService.getWrapperForCompanies(List.of(c));

        assertEquals(1, wrapper.getReadings().size());
    }

    @Test
    void givenWrapperWithReadings_whenUpdateAllReadings_thenInvokeUpdateReadingForEach() {
        ReadingRequest r1 = buildRequest(UUID.randomUUID());
        ReadingRequest r2 = buildRequest(UUID.randomUUID());

        ReadingListWrapper wrapper = new ReadingListWrapper(List.of(r1, r2));

        ReadingService spyService = spy(readingService);

        doNothing().when(spyService).updateReadingForCompany(any());

        spyService.updateAllReadings(wrapper, UUID.randomUUID());

        verify(spyService, times(2)).updateReadingForCompany(any());
    }

    @Test
    void givenAllReadingsWithZeroNewValues_whenAreSwapped_thenReturnTrue() {
        User user = new User();
        Floor floor = new Floor();

        Reading r = new Reading();
        r.setNewReadingM1(BigDecimal.ZERO);
        r.setNewReadingM2(BigDecimal.ZERO);

        when(readingRepository.findAllByCompany_UserAndCompany_Floor(user, floor)).thenReturn(List.of(r));

        when(readingRepository.findAllByCompany_UserAndCompany_FloorAndNewReadingM1AndNewReadingM2(
                user, floor, BigDecimal.ZERO, BigDecimal.ZERO
        )).thenReturn(List.of(r));

        assertTrue(readingService.areSwapped(user, floor));
    }

    @Test
    void givenSomeReadingsHaveNonZeroNewValues_whenAreSwapped_thenReturnFalse() {
        User user = new User();
        Floor floor = new Floor();

        Reading all = new Reading();
        Reading onlyZero = new Reading();
        onlyZero.setNewReadingM1(BigDecimal.ZERO);
        onlyZero.setNewReadingM2(BigDecimal.ZERO);

        when(readingRepository.findAllByCompany_UserAndCompany_Floor(user, floor))
                .thenReturn(List.of(all, onlyZero));

        when(readingRepository.findAllByCompany_UserAndCompany_FloorAndNewReadingM1AndNewReadingM2(
                user, floor, BigDecimal.ZERO, BigDecimal.ZERO
        )).thenReturn(List.of(onlyZero));

        assertFalse(readingService.areSwapped(user, floor));
    }

    private ReadingRequest buildRequest(UUID id) {
        ReadingRequest r = new ReadingRequest();
        r.setCompanyId(id);
        r.setOffice("");
        r.setOldReadingM1(BigDecimal.ONE);
        r.setNewReadingM1(BigDecimal.TEN);
        r.setDifferenceM1(BigDecimal.valueOf(9));
        r.setOldReadingM2(BigDecimal.ONE);
        r.setNewReadingM2(BigDecimal.valueOf(5));
        r.setDifferenceM2(BigDecimal.valueOf(4));
        r.setTotalConsumption(BigDecimal.valueOf(9));
        r.setTotalCost(BigDecimal.valueOf(20));

        return r;
    }
}