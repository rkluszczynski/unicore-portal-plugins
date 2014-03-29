package pl.plgrid.unicore.common.ui;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import eu.unicore.portal.core.GlobalState;
import eu.unicore.portal.core.Session;
import eu.unicore.portal.core.threads.BackgroundWorker;
import eu.unicore.portal.ui.Styles;
import org.apache.log4j.Logger;
import org.w3.x2005.x08.addressing.EndpointReferenceType;
import pl.plgrid.unicore.common.GridServiceExplorer;
import pl.plgrid.unicore.common.i18n.UIComponentsI18N;
import pl.plgrid.unicore.common.services.TargetSystemService;
import pl.plgrid.unicore.common.ui.workers.JobsTableViewerWorker;


public class JobsTableViewer extends VerticalLayout implements
        ValueChangeListener {

    private static final Logger logger = Logger.getLogger(JobsTableViewer.class);

    private final TargetSystemService targetSystemService;
    private Table table;
    private final Button showJobDirButton = new Button("Show Job's Dir");
    private final Label footerLabel = new Label("Selected: -");

    public JobsTableViewer() {
        super();
        initializeComponents();

        GridServiceExplorer gridExplorer
                = Session.getCurrent().getServiceRegistry().getService(GridServiceExplorer.class);
        targetSystemService = gridExplorer.getTargetSystemService();
        reloadJobsList();
    }

    public final void reloadJobsList() {
        BackgroundWorker w = new JobsTableViewerWorker(table, targetSystemService);
        w.schedule();
    }

    @Override
    public void valueChange(Property.ValueChangeEvent event) {
        footerLabel.setValue("Selected: " + table.getValue());
    }

    private void initializeComponents() {
        String tableTitle = getMessage("tableTitle");
        String jobStatusColumnTitle = getMessage("column.statusTitle");
        String jobNameColumnTitle = getMessage("column.nameTitle");
        String jobURIColumnTitle = getMessage("column.uriTitle");
        String jobDirURIColumnTitle = getMessage("column.uriDirTitle");

        table = new Table(tableTitle + " ("
                + Session.getCurrent().getUser().getUsername() + ")");

        table.addContainerProperty(jobStatusColumnTitle, String.class, null);
        table.addContainerProperty(jobNameColumnTitle, String.class, null);
        table.addContainerProperty(jobURIColumnTitle, String.class, null);
        table.addContainerProperty(jobDirURIColumnTitle, String.class, null);

        table.setSelectable(true);
        table.setImmediate(true);
        table.setSizeFull();

        table.addValueChangeListener(this);

        showJobDirButton.setStyleName(Styles.MARGIN_TOP_BOTTOM_15);
        showJobDirButton.addClickListener(new ShowJobDirButtonListener());

        addComponent(showJobDirButton);
        addComponent(table);
        addComponent(footerLabel);
    }

    private String getMessage(String messageKey) {
        return GlobalState.getMessage(UIComponentsI18N.ID, "jobsTableViewer." + messageKey);
    }

    private EndpointReferenceType getSelectedJobDirectoryEpr() {
        Object selectedRowObject = table.getValue();
        if (selectedRowObject == null) {
            return null;
        }
        String jobEprValue = (String) table
                .getContainerProperty(selectedRowObject, "DirURI")
                .getValue();
        EndpointReferenceType jobEpr = EndpointReferenceType.Factory
                .newInstance();
        jobEpr.addNewAddress().setStringValue(jobEprValue);
        return jobEpr;
    }

    private class ShowJobDirButtonListener implements Button.ClickListener {
        @Override
        public void buttonClick(Button.ClickEvent event) {
            EndpointReferenceType selectedJobEpr = getSelectedJobDirectoryEpr();
            if (selectedJobEpr != null) {
                JobDirectoryViewer jdv = new JobDirectoryViewer(selectedJobEpr);
                jdv.showWindow();
            }
        }

    }
}
