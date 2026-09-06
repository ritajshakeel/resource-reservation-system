package com.ritajshakeel.rrs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.timing.Pause.pause;

import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.assertj.swing.timing.Condition;
import org.assertj.swing.timing.Timeout;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.PostgreSQLContainer;

import com.google.inject.Guice;
import com.google.inject.Injector;

import com.ritajshakeel.rrs.domain.Reservation;
import com.ritajshakeel.rrs.domain.ReservationStatus;
import com.ritajshakeel.rrs.guice.RRSModule;
import com.ritajshakeel.rrs.view.swing.RRSSwingView;

@RunWith(GUITestRunner.class)
public class RRSApplicationE2EIT extends AssertJSwingJUnitTestCase {

    @ClassRule
    public static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    private FrameFixture window;
    private EntityManagerFactory entityManagerFactory;

    @Override
    protected void onSetUp() {
        Injector injector = Guice.createInjector(new RRSModule()
            .dbUrl(postgres.getJdbcUrl())
            .dbUsername(postgres.getUsername())
            .dbPassword(postgres.getPassword()));

        entityManagerFactory = injector.getInstance(EntityManagerFactory.class);

        RRSSwingView view = GuiActionRunner.execute(() -> injector.getInstance(RRSSwingView.class));

        window = new FrameFixture(robot(), view);
        window.show();
    }

    @Override
    protected void onTearDown() {
        entityManagerFactory.close();
    }

    @Test
    public void testFullApplicationFlow_registerBookAndConfirm() {
    	window.textBox("nameTextField").click();
    	window.textBox("nameTextField").enterText("Alice");
        window.button("registerUserButton").click();

        pause(new Condition("user registered and added to acting-as combo box") {
            @Override
            public boolean test() {
                return window.comboBox("actingAsComboBox").target().getItemCount() == 1;
            }
        }, Timeout.timeout(5000));

        window.tabbedPane("tabbedPane").selectTab("Resources");
        window.textBox("resourceNameTextField").click();
        window.textBox("resourceNameTextField").enterText("Meeting Room A");
        window.button("registerResourceButton").click();

        pause(new Condition("resource registered and added to resources list") {
            @Override
            public boolean test() {
                return window.list("resourcesList").target().getModel().getSize() == 1;
            }
        }, Timeout.timeout(5000));

        window.tabbedPane("tabbedPane").selectTab("Book");
        window.comboBox("resourceComboBox").selectItem(0);
        window.comboBox("actingAsComboBox").selectItem(0);
        window.button("bookButton").click();

        pause(new Condition("reservation booked") {
            @Override
            public boolean test() {
                return window.label("bookErrorLabel").target().getText().startsWith("Booked");
            }
        }, Timeout.timeout(5000));

        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            List<Reservation> reservations = entityManager.createQuery(
                    "SELECT r FROM Reservation r WHERE r.user.name = :name", Reservation.class)
                .setParameter("name", "Alice")
                .getResultList();
            assertThat(reservations).hasSize(1);
            assertThat(reservations.get(0).getStatus()).isEqualTo(ReservationStatus.PENDING);
        }

        window.tabbedPane("tabbedPane").selectTab("My Reservations");

        pause(new Condition("reservation appears in My Reservations list") {
            @Override
            public boolean test() {
                return window.list("reservationsList").target().getModel().getSize() == 1;
            }
        }, Timeout.timeout(5000));

        window.list("reservationsList").selectItem(0);
        window.button("confirmReservationButton").click();

        pause(new Condition("reservation confirmed") {
            @Override
            public boolean test() {
                return window.label("reservationsErrorLabel").target().getText().startsWith("Confirmed");
            }
        }, Timeout.timeout(5000));

        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            Reservation confirmed = entityManager.createQuery(
                    "SELECT r FROM Reservation r WHERE r.user.name = :name", Reservation.class)
                .setParameter("name", "Alice")
                .getSingleResult();
            assertThat(confirmed.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        }
    }
}