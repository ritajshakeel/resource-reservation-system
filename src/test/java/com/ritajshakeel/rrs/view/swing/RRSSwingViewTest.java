package com.ritajshakeel.rrs.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.swing.JTabbedPane;

import com.ritajshakeel.rrs.controller.RRSController;
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
        window.button(JButtonMatcher.withText("Register")).requireDisabled();
    }

    @Test
    public void testRegisterButtonIsEnabledWhenNameIsEntered() {
        window.textBox("nameTextField").enterText("Alice");

        window.button(JButtonMatcher.withText("Register")).requireEnabled();
    }

    @Test
    public void testClickingRegisterButtonCallsControllerWithEnteredName() {
        window.textBox("nameTextField").enterText("Alice");
        window.button(JButtonMatcher.withText("Register")).click();

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
        window.button(JButtonMatcher.withText("Register")).click();

        GuiActionRunner.execute(() -> rrsSwingView.userRegistered(new User("Alice")));

        window.comboBox("actingAsComboBox").requireItemCount(1);
    }
}