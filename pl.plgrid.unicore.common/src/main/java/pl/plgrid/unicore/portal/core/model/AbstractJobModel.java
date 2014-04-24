package pl.plgrid.unicore.portal.core.model;

import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.vaadin.ui.Component;
import pl.plgrid.unicore.portal.core.ui.model.InputFilesComponent;
import pl.plgrid.unicore.portal.core.ui.model.OutputFilesComponent;
import pl.plgrid.unicore.portal.core.ui.model.ParametersComponent;
import pl.plgrid.unicore.portal.core.ui.model.ResourcesComponent;

import java.util.List;

/**
 * Class representing abstract job.
 *
 * @author rkluszczynski
 */
abstract class AbstractJobModel {

    private final List<ParametersComponent> parametersComponentList = Lists.newArrayList();
    private final List<ResourcesComponent> resourcesComponentList = Lists.newArrayList();
    private final List<InputFilesComponent> inputFilesComponentList = Lists.newArrayList();
    private final List<OutputFilesComponent> outputFilesComponentsList = Lists.newArrayList();


    /**
     * Registers component for data extraction during job submission.
     *
     * @param component Component implementing one of interfaces.
     */
    public void registerComponent(Component component) {
        if (component instanceof ParametersComponent) {
            parametersComponentList.add((ParametersComponent) component);
        }
        if (component instanceof ResourcesComponent) {
            resourcesComponentList.add((ResourcesComponent) component);
        }
        if (component instanceof InputFilesComponent) {
            inputFilesComponentList.add((InputFilesComponent) component);
        }
        if (component instanceof OutputFilesComponent) {
            outputFilesComponentsList.add((OutputFilesComponent) component);
        }
    }
}
