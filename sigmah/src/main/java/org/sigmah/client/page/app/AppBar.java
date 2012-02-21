package org.sigmah.client.page.app;

import org.sigmah.client.EventBus;
import org.sigmah.client.event.NavigationEvent;
import org.sigmah.client.page.NavigationHandler;
import org.sigmah.client.page.search.SearchPageState;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class AppBar extends Composite  {

	private static AppBarUiBinder uiBinder = GWT.create(AppBarUiBinder.class);

	@UiField
	SectionTabStrip sectionTabStrip;

	@UiField
	Label logo;
	
	@UiField
	Label settingsButton;
	
	@UiField
	Label searchButton;

	
	private SettingsPopup settingsPopup;

	private EventBus eventBus;
	
	public static int HEIGHT = 50;

	interface AppBarUiBinder extends UiBinder<Widget, AppBar> {
	}
	
	@Inject
	public AppBar(EventBus eventBus) {
		this.eventBus = eventBus;
		initWidget(uiBinder.createAndBindUi(this));
	}


	public AppBar(String firstName) {
		initWidget(uiBinder.createAndBindUi(this));
	}


	public SectionTabStrip getSectionTabStrip() {
		return sectionTabStrip;
	}

	@UiHandler("logo")
	void handleLogoClick(ClickEvent e) {
		Window.open("http://www.activityinfo.org/content/", "_blank", null);
	}
	
	@UiHandler("settingsButton")
	void handleSettingsClick(ClickEvent e) {
		if(settingsPopup == null) {
			settingsPopup = new SettingsPopup();
		}
		settingsPopup.setPopupPosition(Window.getClientWidth() - SettingsPopup.WIDTH, HEIGHT-3);
		settingsPopup.show();
	}
	
	@UiHandler("searchButton")
	void handleSearchClick(ClickEvent e) {
		eventBus.fireEvent(new NavigationEvent(NavigationHandler.NavigationRequested, new SearchPageState()));
	}
}
