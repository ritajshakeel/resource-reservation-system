package com.ritajshakeel.rrs.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.swing.JTabbedPane;

import com.ritajshakeel.rrs.controller.RRSController;
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
    public void testShowErrorSetsErrorLabelText() {
        GuiActionRunner.execute(() -> rrsSwingView.showError("Something went wrong"));

        window.label("errorLabel").requireText("Something went wrong");
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
        window.comboBox("resourceComboBox").requireItemCount(2);
    }

    @Test
    public void testResourceRegisteredUpdatesListComboBoxAndResetsError() {
        window.tabbedPane("tabbedPane").selectTab("Resources");

        GuiActionRunner.execute(() -> rrsSwingView.resourceRegistered(new Resource("Meeting Room C")));

        assertThat(window.list("resourcesList").contents()).containsExactly("Meeting Room C");
        window.comboBox("resourceComboBox").requireItemCount(1);
        window.label("resourceErrorLabel").requireText(" ");
    }
}