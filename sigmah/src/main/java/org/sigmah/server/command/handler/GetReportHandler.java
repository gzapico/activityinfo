package org.sigmah.server.command.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.xml.bind.JAXBException;

import org.sigmah.server.database.hibernate.entity.ReportDefinition;
import org.sigmah.server.database.hibernate.entity.ReportSubscription;
import org.sigmah.server.database.hibernate.entity.User;
import org.sigmah.server.report.ReportParserJaxb;
import org.sigmah.shared.command.GetReport;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.command.result.ReportTemplateResult;
import org.sigmah.shared.dto.ReportDefinitionDTO;
import org.sigmah.shared.exception.CommandException;
import org.sigmah.shared.report.model.Report;
import org.sigmah.shared.report.model.ReportElement;
import org.sigmah.shared.report.model.TableElement;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class GetReportHandler implements CommandHandler<GetReport> {
	private EntityManager em;

	@Inject
	public GetReportHandler(EntityManager em) {
		this.em = em;
	}

	@Override
	public CommandResult execute(GetReport cmd, User user)
			throws CommandException {

		Query query = em
				.createQuery(
						"select r from ReportDefinition r where r.id in (:id)")
				.setParameter("id", cmd.getReportTemplateId());

		ReportDefinition result = (ReportDefinition) query.getSingleResult();

		List<ReportDefinitionDTO> dtos = new ArrayList<ReportDefinitionDTO>();
		List<String> emails = new ArrayList<String>(); 

		try {
			ReportDefinitionDTO dto = new ReportDefinitionDTO();
			dto.setId(result.getId());
			dto.setOwnerName(result.getOwner().getName());
			dto.setAmOwner(result.getOwner().getId() == user.getId());
			dto.setTitle(result.getTitle());
			dto.setFrequency(result.getFrequency());
			dto.setDay(result.getDay());
			dto.setDescription(result.getDescription());
			dto.setEditAllowed(dto.getAmOwner());
			dto.setSubscribed(true);
			
			for(ReportSubscription sub : result.getSubscriptions()){
				emails.add(sub.getUser().getEmail());
			}
			dto.setSubscribers(emails);
			
			Report report = new Report();
			report = ReportParserJaxb.parseXml(result.getXml());

			List<TableElement> tbles = Lists.newArrayList();
			
			for(ReportElement re : report.getElements()){
				if(re instanceof TableElement){
					tbles.add((TableElement)re);
					continue;
				}
			}
			report.getElements().removeAll(tbles);
			dto.setReport(report);
			
			dtos.add(dto);
			
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return new ReportTemplateResult(dtos);
	}

}

