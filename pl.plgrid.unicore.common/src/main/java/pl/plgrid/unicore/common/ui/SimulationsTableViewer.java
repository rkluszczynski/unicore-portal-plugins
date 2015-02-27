package pl.plgrid.unicore.common.ui;

import com.vaadin.server.Resource;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.BaseTheme;
import de.fzj.unicore.uas.client.JobClient;
import eu.unicore.portal.core.GlobalState;
import eu.unicore.portal.core.threads.BackgroundWorker;
import eu.unicore.portal.ui.IconUtil;
import eu.unicore.portal.ui.icons.IconRepository;
import org.apache.log4j.Logger;
import org.w3.x2005.x08.addressing.EndpointReferenceType;
import pl.plgrid.unicore.common.ui.model.SimulationViewerData;
import pl.plgrid.unicore.common.ui.workers.JobsTableViewerWorker;
import pl.plgrid.unicore.portal.core.i18n.ComponentsI18N;
import pl.plgrid.unicore.portal.core.utils.SecurityHelper;


public class SimulationsTableViewer extends CustomComponent {
    private static final Logger logger = Logger.getLogger(SimulationsTableViewer.class);

    private final Table table = new Table();

    public SimulationsTableViewer() {
        super();
        initializeComponents();

        reloadJobsList();
    }

    public final void reloadJobsList() {
        BackgroundWorker w = new JobsTableViewerWorker(table);
        w.schedule();
    }


    private void initializeComponents() {
        String columnStatus = "column.status";
        String columnJobName = "column.jobName";
        String columnSubmissionTime = "column.submissionTime";

        table.addContainerProperty(columnStatus, String.class, null);
        table.addContainerProperty(columnJobName, String.class, null);
        table.addContainerProperty(columnSubmissionTime, String.class, null);

        table.setColumnHeader(columnStatus, getMessage(String.format("%sTitle", columnStatus)));
        table.setColumnHeader(columnJobName, getMessage(String.format("%sTitle", columnJobName)));
        table.setColumnHeader(columnSubmissionTime, getMessage(String.format("%sTitle", columnSubmissionTime)));

        table.setSelectable(true);
        table.setImmediate(true);
        table.setSizeFull();

        Button refreshJobsListButton = createButtonInstance(
                getMessage("jobs.refreshList"),
                IconUtil.getIconFromTheme(IconRepository.ICON_ID_REFRESH),
                new RefreshJobsListButtonListener()
        );
        Button showJobDirButton = createButtonInstance(
                getMessage("jobs.openDir"),
                IconUtil.getIconFromTheme(IconRepository.ICON_ID_EXPLORE_FOLDER),
                new ShowJobDirButtonListener()
        );
        Button destroySimulationButton = createButtonInstance(
                getMessage("jobs.destroy"),
                IconUtil.getIconFromTheme(IconRepository.ICON_ID_DELETE),
                new DestroySimulationButtonListener()
        );

        HorizontalLayout horizontalLayout = new HorizontalLayout(
                showJobDirButton, refreshJobsListButton, destroySimulationButton
        );
        horizontalLayout.setSpacing(true);

        GridLayout gridLayout = new GridLayout(1, 2);
        gridLayout.addComponent(horizontalLayout, 0, 0);
        gridLayout.setComponentAlignment(horizontalLayout, Alignment.TOP_RIGHT);
        gridLayout.addComponent(table, 0, 1);
        gridLayout.setRowExpandRatio(1, 1.f);
        gridLayout.setSpacing(true);
        gridLayout.setSizeFull();

        setCompositionRoot(gridLayout);
        setSizeFull();
    }

    private Button createButtonInstance(String message, Resource icon, Button.ClickListener tableViewerButtonListener) {
        Button tableViewerButton = new Button(message);
//        tableViewerButton.setStyleName(Reindeer.BUTTON_SMALL);
        tableViewerButton.setEnabled(true);
        tableViewerButton.setImmediate(true);
        tableViewerButton.setStyleName(BaseTheme.BUTTON_LINK);
        tableViewerButton.setIcon(icon);
        tableViewerButton.setCaption("");
        tableViewerButton.setDescription(message);
        tableViewerButton.addClickListener(tableViewerButtonListener);
        return tableViewerButton;
    }

    private String getMessage(String messageKey) {
        return GlobalState.getMessage(ComponentsI18N.ID, "simulationsTableViewer." + messageKey);
    }

    private SimulationViewerData getSelectedSimulationViewerData() {
        Object selectedRowObject = table.getValue();
        if (selectedRowObject == null) {
            return null;
        }
        return (SimulationViewerData) selectedRowObject;
    }

    private class DestroySimulationButtonListener implements Button.ClickListener {
        @Override
        public void buttonClick(Button.ClickEvent event) {
            SimulationViewerData simulationViewerData = getSelectedSimulationViewerData();
            if (simulationViewerData != null) {
                EndpointReferenceType simulationEpr = simulationViewerData
                        .getSimulationEpr();
                try {
                    new JobClient(simulationEpr, SecurityHelper.getClientConfig())
                            .destroy();
                    reloadJobsList();
                } catch (Exception e) {
                    String message = String.format("Unable to destroy job: %s",
                            (simulationEpr == null) ? "<Epr not retrieved>" : simulationEpr.getAddress().getStringValue());
                    logger.warn(message, e);
                    Notification.show(message, Notification.Type.WARNING_MESSAGE);
                }
            }
        }
    }

    private class ShowJobDirButtonListener implements Button.ClickListener {
        @Override
        public void buttonClick(Button.ClickEvent event) {
            SimulationViewerData simulationViewerData = getSelectedSimulationViewerData();
            if (simulationViewerData != null) {
                GridDirectoryViewer jdv = new GridDirectoryViewer(
                        simulationViewerData
                                .getDirectoryEpr()
                );
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
