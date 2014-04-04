package pl.plgrid.unicore.common.ui;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import eu.unicore.portal.core.GlobalState;
import eu.unicore.portal.core.Session;
import eu.unicore.portal.core.threads.BackgroundWorker;
import org.apache.log4j.Logger;
import org.w3.x2005.x08.addressing.EndpointReferenceType;
import pl.plgrid.unicore.common.GridServicesExplorer;
import pl.plgrid.unicore.common.i18n.CommonComponentsI18N;
import pl.plgrid.unicore.common.services.TargetSystemService;
import pl.plgrid.unicore.common.ui.workers.JobsTableViewerWorker;


public class JobsTableViewer extends CustomComponent implements
        ValueChangeListener {

    private static final Logger logger = Logger.getLogger(JobsTableViewer.class);

    private final TargetSystemService targetSystemService;

    private final Label footerLabel = new Label("Selected: -");
    private Table table;

    public JobsTableViewer() {
        super();
        initializeComponents();

        GridServicesExplorer gridExplorer
                = Session.getCurrent().getServiceRegistry().getService(GridServicesExplorer.class);
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

        Button refreshJobsListButton = new Button(getMessage("jobs.refreshList"));
        refreshJobsListButton.setStyleName(Reindeer.BUTTON_SMALL);
        refreshJobsListButton.addClickListener(new RefreshJobsListButtonListener());

        Button showJobDirButton = new Button(getMessage("jobs.openDir"));
        showJobDirButton.setStyleName(Reindeer.BUTTON_SMALL);
        showJobDirButton.addClickListener(new ShowJobDirButtonListener());

        footerLabel.setStyleName(Reindeer.LABEL_SMALL);
        HorizontalLayout horizontalLayout = new HorizontalLayout(
                refreshJobsListButton, showJobDirButton, footerLabel
        );
//        horizontalLayout.setMargin(true);
        horizontalLayout.setSpacing(true);
//        horizontalLayout.setSizeFull();
//        horizontalLayout.setHeight(100, Unit.PIXELS);


        GridLayout gridLayout = new GridLayout(1, 2);
        gridLayout.addComponent(horizontalLayout, 0, 0);
        gridLayout.addComponent(table, 0, 1);
        gridLayout.setRowExpandRatio(1, 1.f);
        gridLayout.setSizeFull();

//        VerticalLayout verticalLayout = new VerticalLayout(
//                horizontalLayout, table
//        );
//        verticalLayout.setExpandRatio(horizontalLayout, 1);
//        verticalLayout.setExpandRatio(table, 100);
//        verticalLayout.setSizeFull();

        setCompositionRoot(gridLayout);
        setSizeFull();
    }

    private String getMessage(String messageKey) {
        return GlobalState.getMessage(CommonComponentsI18N.ID, "jobsTableViewer." + messageKey);
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

    private class RefreshJobsListButtonListener implements Button.ClickListener {
        @Override
        public void buttonClick(Button.ClickEvent event) {
            reloadJobsList();
        }
    }
}
