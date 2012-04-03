package org.sigmah.shared.command;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sigmah.server.command.CommandTestCase2;
import org.sigmah.server.database.OnDataSet;
import org.sigmah.server.database.TestDatabaseModule;
import org.sigmah.server.report.ReportModule;
import org.sigmah.shared.command.result.ReportVisibilityResult;
import org.sigmah.shared.command.result.ReportsResult;
import org.sigmah.shared.dto.ReportMetadataDTO;
import org.sigmah.shared.dto.ReportVisibilityDTO;
import org.sigmah.test.InjectionSupport;
import org.sigmah.test.Modules;

@RunWith(InjectionSupport.class)
@Modules({TestDatabaseModule.class, ReportModule.class})
@OnDataSet("/dbunit/schema1.db.xml")
public class ReportVisibilityTest extends CommandTestCase2 {

	@Test
	public void update() {
		
		setUser(1);
		
		ReportVisibilityDTO db1 = new ReportVisibilityDTO();
		db1.setDatabaseId(1);
		db1.setVisible(true);
		db1.setDefaultDashboard(true);
		
		ReportVisibilityDTO db2 = new ReportVisibilityDTO();
		db2.setDatabaseId(2);
		db2.setVisible(false);
		
		ReportVisibilityDTO db3 = new ReportVisibilityDTO();
		db3.setDatabaseId(3);
		db3.setVisible(true);
		
		UpdateReportVisibility update = new UpdateReportVisibility(1, Arrays.asList(db1, db2, db3));
		execute(update);
		
		ReportVisibilityResult result = execute(new GetReportVisibility(1));
		assertThat(result.getList().size(), equalTo(2));
		
		// make sure we can still see the report
		
		ReportsResult visibleToMe = execute(new GetReports());
		assertThat(visibleToMe.getData().size(), equalTo(1));

		
		
		setUser(2); // Bavon
		
		ReportsResult visibleToBavon = execute(new GetReports());
		assertThat(visibleToBavon.getData().size(), equalTo(2));
		assertThat(getById(visibleToBavon, 1).isDashboard(), equalTo(true));
		assertThat(getById(visibleToBavon, 2).isDashboard(), equalTo(false));


		setUser(3); // Stefan, no access to db
		
		ReportsResult visibleToStefan = execute(new GetReports());
		assertThat(visibleToStefan.getData().size(), equalTo(0));
		
		
	}
	
	private ReportMetadataDTO getById(ReportsResult result, int id) {
		for(ReportMetadataDTO dto : result.getData()) {
			if(dto.getId() == id) {
				return dto;
			}
		}
		throw new AssertionError("no report with id " + id);
	}
}