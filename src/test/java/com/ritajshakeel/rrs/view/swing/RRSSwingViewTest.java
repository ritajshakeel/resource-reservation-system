package com.ritajshakeel.rrs.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.swing.JTabbedPane;

import com.ritajshakeel.rrs.controller.RRSController;
import com.ritajshakeel.rrs.domain.Reservation;
import com.ritajshakeel.rrs.domain.Resource;
import com.ritajshakeel.rrs.domain.User;

@RunWith(GUITestRunner.class)
public class RRSSwingViewTest extends AssertJSwingJUnitTestCase {

    private FrameFixture window;
    private RRSSwingView rrsSwingView;
    private RRSController controller;

    @Override
    protected void onSetUp() {
        controller = mock(RRSController.class);
        GuiActionRunner.execute(() -> {
            rrsSwingView = new RRSSwingView();
            rrsSwingView.setController(controller);
            return rrsSwingView;
        });
        window = new FrameFixture(robot(), rrsSwingView);
        window.show();
    }

    @Test
    public void testInitialControlStates() {
        window.textBox("nameTextField").requireEnabled();
        window.button("registerUserButton").requireDisabled();
    }

    @Test
    public void testRegisterButtonIsEnabledWhenNameIsEntered() {
        window.textBox("nameTextField").enterText("Alice");

        window.button("registerUserButton").requireEnabled();
    }

    @Test
    public void testClickingRegisterButtonCallsControllerWithEnteredName() {
        window.textBox("nameTextField").enterText("Alice");
        window.button("registerUserButton").click();

        verify(controller).registerUser("Alice");
    }
    
    @Test
    public void testTabsArePresent() {
        JTabbedPane tabbedPane = (JTabbedPane) window.tabbedPane("tabbedPane").target();
        assertThat(tabbedPane.getTabCount()).isEqualTo(4);
        assertThat(tabbedPane.getTitleAt(0)).isEqualTo("Register");
        assertThat(tabbedPane.getTitleAt(1)).isEqualTo("Resources");
        assertThat(tabbedPane.getTitleAt(2)).isEqualTo("Book");
        assertThat(tabbedPane.getTitleAt(3)).isEqualTo("My Reservations");
    }

    @Test
    public void testActingAsComboBoxIsPresentAndInitiallyEmpty() {
        window.comboBox("actingAsComboBox").requireItemCount(0);
    }
    
    @Test
    public void testRegisteringUserAddsThemToActingAsComboBox() {
        window.textBox("nameTextField").enterText("Alice");
        window.button("registerUserButton").click();

        GuiActionRunner.execute(() -> rrsSwingView.userRegistered(new User("Alice")));

        window.comboBox("actingAsComboBox").requireItemCount(1);
    }

    @Test
    public void testUsersListedPopulatesActingAsComboBox() {
        GuiActionRunner.execute(() -> rrsSwingView.usersListed(
            List.of(new User("Alice"), new User("Bob"))));

        window.comboBox("actingAsComboBox").requireItemCount(2);
    }
    
    @Test
    public void testResourcesListedPopulatesListAndComboBox() {
        window.tabbedPane("tabbedPane").selectTab("Resources");
        Resource roomA = new Resource("Meeting Room A");
        Resource roomB = new Resource("Meeting Room B");

        GuiActionRunner.execute(() -> rrsSwingView.resourcesListed(List.of(roomA, roomB)));

        assertThat(window.list("resourcesList").contents()).containsExactly("Meeting Room A", "Meeting Room B");
        window.tabbedPane("tabbedPane").selectTab("Book");
        window.comboBox("resourceComboBox").requireItemCount(2);
    }

    @Test
    public void testResourceRegisteredUpdatesListComboBoxAndShowsSuccessMessage() {
        window.tabbedPane("tabbedPane").selectTab("Resources");

        GuiActionRunner.execute(() -> rrsSwingView.resourceRegistered(new Resource("Meeting Room C")));

        assertThat(window.list("resourcesList").contents()).containsExactly("Meeting Room C");
        window.label("resourceErrorLabel").requireText("Registered \"Meeting Room C\".");
        window.tabbedPane("tabbedPane").selectTab("Book");
        window.comboBox("resourceComboBox").requireItemCount(1);
    }
    
    @Test
    public void testBookButtonDisabledUntilResourceSelected() {
        window.tabbedPane("tabbedPane").selectTab("Book");

        window.button("bookButton").requireDisabled();
    }

    @Test
    public void testClickingBookButtonCallsControllerWithSelectedValues() {
        Resource resource = new Resource("Meeting Room A");
        User user = new User("Alice");
        window.tabbedPane("tabbedPane").selectTab("Resources");
        GuiActionRunner.execute(() -> rrsSwingView.resourceRegistered(resource));
        GuiActionRunner.execute(() -> rrsSwingView.userRegistered(user));
        window.tabbedPane("tabbedPane").selectTab("Book");
        window.comboBox("resourceComboBox").selectItem(0);
        window.comboBox("actingAsComboBox").selectItem(0);

        window.button("bookButton").click();

        verify(controller).bookReservation(eq(user), eq(resource), any(), any());
    }
    
    @Test
    public void testRegisterResourceButtonIsEnabledWhenNameIsEntered() {
        window.tabbedPane("tabbedPane").selectTab("Resources");

        window.textBox("resourceNameTextField").enterText("Meeting Room A");

        window.button("registerResourceButton").requireEnabled();
    }
    
    @Test
    public void testGetControllerReturnsSetController() {
        assertThat(rrsSwingView.getController()).isSameAs(controller);
    }
    
    @Test
    public void testBookButtonStaysDisabledWithResourceButNoUser() {
        window.tabbedPane("tabbedPane").selectTab("Resources");
        GuiActionRunner.execute(() -> rrsSwingView.resourceRegistered(new Resource("Meeting Room A")));
        window.tabbedPane("tabbedPane").selectTab("Book");

        window.comboBox("resourceComboBox").selectItem(0);

        window.button("bookButton").requireDisabled();
    }

    @Test
    public void testBookButtonEnabledWithBothResourceAndUserSelected() {
        window.tabbedPane("tabbedPane").selectTab("Resources");
        GuiActionRunner.execute(() -> rrsSwingView.resourceRegistered(new Resource("Meeting Room A")));
        GuiActionRunner.execute(() -> rrsSwingView.userRegistered(new User("Alice")));
        window.tabbedPane("tabbedPane").selectTab("Book");
        window.comboBox("resourceComboBox").selectItem(0);
        window.comboBox("actingAsComboBox").selectItem(0);

        window.button("bookButton").requireEnabled();
    }
    
    @Test
    public void testRegisterButtonDisabledAgainWhenNameIsCleared() {
        window.textBox("nameTextField").enterText("Alice");
        window.textBox("nameTextField").deleteText();

        window.button("registerUserButton").requireDisabled();
    }

    @Test
    public void testRegisterResourceButtonDisabledAgainWhenNameIsCleared() {
        window.tabbedPane("tabbedPane").selectTab("Resources");
        window.textBox("resourceNameTextField").enterText("Meeting Room A");
        window.textBox("resourceNameTextField").deleteText();

        window.button("registerResourceButton").requireDisabled();
    }
    
    @Test
    public void testClickingRegisterResourceButtonCallsControllerWithEnteredName() {
        window.tabbedPane("tabbedPane").selectTab("Resources");
        window.textBox("resourceNameTextField").enterText("Meeting Room A");
        window.button("registerResourceButton").click();

        verify(controller).registerResource("Meeting Room A");
    }
    
    @Test
    public void testSelectingActingAsUserLoadsTheirReservations() {
        window.textBox("nameTextField").enterText("Alice");
        window.button("registerUserButton").click();
        GuiActionRunner.execute(() -> rrsSwingView.userRegistered(new User("Alice")));

        window.comboBox("actingAsComboBox").selectItem(0);

        verify(controller).onActingAsUserSelected(org.mockito.ArgumentMatchers.any(User.class));
    }

    @Test
    public void testReservationsListedPopulatesReservationsList() {
        window.tabbedPane("tabbedPane").selectTab("My Reservations");
        Reservation reservation = mock(Reservation.class);
        when(reservation.toString()).thenReturn("Meeting Room A: 2026-07-12 09:00 - 2026-07-12 10:00 (PENDING)");

        GuiActionRunner.execute(() -> rrsSwingView.reservationsListed(List.of(reservation)));

        assertThat(window.list("reservationsList").contents())
            .containsExactly("Meeting Room A: 2026-07-12 09:00 - 2026-07-12 10:00 (PENDING)");
    }
    
    @Test
    public void testShowBookingErrorSetsBookErrorLabelText() {
        window.tabbedPane("tabbedPane").selectTab("Book");
        GuiActionRunner.execute(() -> rrsSwingView.showBookingError("Something went wrong"));

        window.label("bookErrorLabel").requireText("Something went wrong");
    }
    
    @Test
    public void testBookTabDefaultsEndTimeOneHourAfterStart() {
        window.tabbedPane("tabbedPane").selectTab("Book");

        Date start = (Date) window.spinner("startDateSpinner").target().getValue();
        Date end = (Date) window.spinner("endDateSpinner").target().getValue();

        assertThat(end.getTime() - start.getTime()).isEqualTo(60 * 60 * 1000);
    }

    @Test
    public void testReservationBookedResetsSpinnersWithOneHourGap() {
        window.tabbedPane("tabbedPane").selectTab("Book");

        GuiActionRunner.execute(() -> rrsSwingView.reservationBooked(mock(Reservation.class)));

        Date start = (Date) window.spinner("startDateSpinner").target().getValue();
        Date end = (Date) window.spinner("endDateSpinner").target().getValue();
        assertThat(end.getTime() - start.getTime()).isEqualTo(60 * 60 * 1000);
    }
    
    @Test
    public void testShowRegistrationErrorSetsErrorLabelText() {
        GuiActionRunner.execute(() -> rrsSwingView.showRegistrationError("Name must not be empty"));

        window.label("errorLabel").requireText("Name must not be empty");
    }

    @Test
    public void testShowResourceRegistrationErrorSetsResourceErrorLabelText() {
        window.tabbedPane("tabbedPane").selectTab("Resources");
        GuiActionRunner.execute(() -> rrsSwingView.showResourceRegistrationError("Name must not be empty"));

        window.label("resourceErrorLabel").requireText("Name must not be empty");
    }
    
    @Test
    public void testReservationBookedShowsSuccessMessage() {
        window.tabbedPane("tabbedPane").selectTab("Book");
        Reservation reservation = mock(Reservation.class);
        when(reservation.toString()).thenReturn("Meeting Room A: 2026-07-12 09:00 - 2026-07-12 10:00 (PENDING)");

        GuiActionRunner.execute(() -> rrsSwingView.reservationBooked(reservation));

        window.label("bookErrorLabel").requireText("Booked Meeting Room A: 2026-07-12 09:00 - 2026-07-12 10:00 (PENDING).");
    }
    
    @Test
    public void testReservationConfirmedShowsSuccessMessage() {
        window.tabbedPane("tabbedPane").selectTab("My Reservations");
        Reservation reservation = mock(Reservation.class);
        when(reservation.toString()).thenReturn("Meeting Room A: 2026-07-12 09:00 - 2026-07-12 10:00 (CONFIRMED)");

        GuiActionRunner.execute(() -> rrsSwingView.reservationConfirmed(reservation));

        window.label("reservationsErrorLabel").requireText("Confirmed Meeting Room A: 2026-07-12 09:00 - 2026-07-12 10:00 (CONFIRMED).");
    }

    @Test
    public void testReservationCancelledShowsSuccessMessage() {
        window.tabbedPane("tabbedPane").selectTab("My Reservations");
        Reservation reservation = mock(Reservation.class);
        when(reservation.toString()).thenReturn("Meeting Room A: 2026-07-12 09:00 - 2026-07-12 10:00 (CANCELLED)");

        GuiActionRunner.execute(() -> rrsSwingView.reservationCancelled(reservation));

        window.label("reservationsErrorLabel").requireText("Cancelled Meeting Room A: 2026-07-12 09:00 - 2026-07-12 10:00 (CANCELLED).");
    }
    
    @Test
    public void testShowReservationActionErrorSetsReservationsErrorLabelText() {
        window.tabbedPane("tabbedPane").selectTab("My Reservations");

        GuiActionRunner.execute(() -> rrsSwingView.showReservationActionError("Cannot cancel a cancelled reservation"));

        window.label("reservationsErrorLabel").requireText("Cannot cancel a cancelled reservation");
    }
    
    @Test
    public void testSelectingReservationEnablesConfirmAndCancelButtons() {
        window.tabbedPane("tabbedPane").selectTab("My Reservations");
        Reservation reservation = mock(Reservation.class);
        when(reservation.toString()).thenReturn("Meeting Room A: 2026-07-12 09:00 - 2026-07-12 10:00 (PENDING)");
        GuiActionRunner.execute(() -> rrsSwingView.reservationsListed(List.of(reservation)));

        window.list("reservationsList").selectItem(0);

        window.button("confirmReservationButton").requireEnabled();
        window.button("cancelReservationButton").requireEnabled();
    }

    @Test
    public void testClearingSelectionDisablesConfirmAndCancelButtons() {
        window.tabbedPane("tabbedPane").selectTab("My Reservations");
        Reservation reservation = mock(Reservation.class);
        when(reservation.toString()).thenReturn("Meeting Room A: 2026-07-12 09:00 - 2026-07-12 10:00 (PENDING)");
        GuiActionRunner.execute(() -> rrsSwingView.reservationsListed(List.of(reservation)));
        window.list("reservationsList").selectItem(0);

        window.list("reservationsList").clearSelection();

        window.button("confirmReservationButton").requireDisabled();
        window.button("cancelReservationButton").requireDisabled();
    }
    
    @Test
    public void testClickingConfirmButtonCallsControllerWithSelectedReservationId() {
        window.tabbedPane("tabbedPane").selectTab("My Reservations");
        Reservation reservation = mock(Reservation.class);
        when(reservation.getId()).thenReturn(5L);
        when(reservation.toString()).thenReturn("Meeting Room A: 2026-07-12 09:00 - 2026-07-12 10:00 (PENDING)");
        GuiActionRunner.execute(() -> rrsSwingView.reservationsListed(List.of(reservation)));
        window.list("reservationsList").selectItem(0);

        window.button("confirmReservationButton").click();

        verify(controller).confirmReservation(5L);
    }

    @Test
    public void testClickingCancelButtonCallsControllerWithSelectedReservationId() {
        window.tabbedPane("tabbedPane").selectTab("My Reservations");
        Reservation reservation = mock(Reservation.class);
        when(reservation.getId()).thenReturn(5L);
        when(reservation.toString()).thenReturn("Meeting Room A: 2026-07-12 09:00 - 2026-07-12 10:00 (PENDING)");
        GuiActionRunner.execute(() -> rrsSwingView.reservationsListed(List.of(reservation)));
        window.list("reservationsList").selectItem(0);

        window.button("cancelReservationButton").click();

        verify(controller).cancelReservation(5L);
    }
}