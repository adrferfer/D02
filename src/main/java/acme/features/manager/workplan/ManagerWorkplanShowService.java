package acme.features.manager.workplan;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.entities.roles.Manager;
import acme.entities.workplans.Workplan;
import acme.framework.components.Model;
import acme.framework.components.Request;
import acme.framework.entities.Principal;
import acme.framework.services.AbstractShowService;

@Service
public class ManagerWorkplanShowService implements AbstractShowService<Manager, Workplan> {

	@Autowired
	protected ManagerWorkplanRepository repository;
	
	@Override
	public boolean authorise(final Request<Workplan> request) {
		assert request != null;
		
		boolean res;
		int workplanId;
		final Workplan workplan;
		final Manager manager;
		Principal principal;

		workplanId = request.getModel().getInteger("id");
		workplan = this.repository.findOneWorkplanById(workplanId);
		manager = workplan.getOwner();
		principal = request.getPrincipal();
		res = manager.getUserAccount().getId() == principal.getAccountId();
		
		return res;
	}

	@Override
	public void unbind(final Request<Workplan> request, final Workplan entity, final Model model) {
		assert request != null;
		assert entity != null;
		assert model != null;

		request.unbind(entity, model, "title", "executionPeriodStart", "executionPeriodEnd", "workload", "isPublic");
		
		final SimpleDateFormat formato = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		final Date earliestTask = this.repository.earliestTaskDateFromWorkplan(entity.getId());
		final Calendar aux = Calendar.getInstance();
		aux.setTime(earliestTask);
	    aux.set(Calendar.HOUR, 8);
	    aux.set(Calendar.MINUTE, 0);
		aux.add(Calendar.DAY_OF_MONTH, -1);
	    final Date earliestDate = aux.getTime();
	    final StringBuilder suggestionBuilder = new StringBuilder();
	    suggestionBuilder.append("<"+formato.format(earliestDate)+", ");
	    
	    final Date latestTask = this.repository.latestTaskDateFromWorkplan(entity.getId());
	    aux.setTime(latestTask);
	    aux.set(Calendar.HOUR_OF_DAY, 17);
	    aux.set(Calendar.MINUTE, 0);
		aux.add(Calendar.DAY_OF_MONTH, 1);
	    final Date latestDate = aux.getTime();
	    suggestionBuilder.append(formato.format(latestDate)+">");
	    
		model.setAttribute("suggestion", suggestionBuilder.toString());
		
	}

	@Override
	public Workplan findOne(final Request<Workplan> request) {
		assert request != null;

		Workplan result;
		int id;

		id = request.getModel().getInteger("id");
		result = this.repository.findOneWorkplanById(id);

		return result;
	}

}
