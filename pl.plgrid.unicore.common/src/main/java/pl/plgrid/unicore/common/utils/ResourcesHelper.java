package pl.plgrid.unicore.common.utils;

import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.google.gwt.thirdparty.guava.common.collect.Maps;
import org.ggf.schemas.jsdl.x2005.x11.jsdl.CPUArchitectureType;
import org.ggf.schemas.jsdl.x2005.x11.jsdl.OperatingSystemType;
import org.ggf.schemas.jsdl.x2005.x11.jsdl.RangeValueType;
import org.unigrids.services.atomic.types.AvailableResourceType;
import org.unigrids.services.atomic.types.AvailableResourceTypeType;
import org.unigrids.services.atomic.types.ProcessorType;
import pl.plgrid.unicore.common.resources.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.unigrids.x2006.x04.services.tss.TargetSystemPropertiesDocument.TargetSystemProperties;


public class ResourcesHelper {

    public static Collection<? extends AvailableResource> convertAvailableResources(AvailableResourceType[] availableResourceArray) {
        // TODO: handle default option
        // FIXME: problems with parsing min and max
        List<AvailableResource> availableResources = Lists.newArrayList();
        for (AvailableResourceType availableResourceType : availableResourceArray) {
            AvailableResource availableResource;

            switch (availableResourceType.getType().intValue()) {
                case AvailableResourceTypeType.INT_CHOICE:
                    availableResource = new AvailableEnumResource(
                            availableResourceType.getName(),
                            availableResourceType.getDescription(),
                            availableResourceType.getDefault(),
                            availableResourceType.getAllowedValueArray()
                    );
                    break;
                case AvailableResourceTypeType.INT_BOOLEAN:
                    availableResource = new AvailableBooleanResource(
                            availableResourceType.getName(),
                            availableResourceType.getDescription(),
                            availableResourceType.getDefault()
                    );
                    break;
                case AvailableResourceTypeType.INT_DOUBLE:
                    availableResource = new AvailableDoubleResource(
                            availableResourceType.getName(),
                            availableResourceType.getDescription(),
                            availableResourceType.getDefault(),
                            Double.parseDouble(availableResourceType.getMin()),
                            Double.parseDouble(availableResourceType.getMax())
                    );
                    break;
                case AvailableResourceTypeType.INT_INT:
                    availableResource = new AvailableIntResource(
                            availableResourceType.getName(),
                            availableResourceType.getDescription(),
                            availableResourceType.getDefault(),
                            Integer.parseInt(availableResourceType.getMin()),
                            Integer.parseInt(availableResourceType.getMax())
                    );
                    break;
                case AvailableResourceTypeType.INT_STRING:
                default:
                    availableResource = new AvailableStringResource(
                            availableResourceType.getName(),
                            availableResourceType.getDescription(),
                            availableResourceType.getDefault()
                    );
            }

            availableResources.add(availableResource);
        }
        return availableResources;
    }


    public static Collection<? extends AvailableResource> convertStandardResources(TargetSystemProperties targetSystemProperties) {
        List<AvailableResource> availableResources = Lists.newArrayList();
        Map<StandardResources, RangeValueType> rangeValueTypeIntResources = Maps.newHashMap();

        if (targetSystemProperties.isSetOperatingSystem()) {
            OperatingSystemType osType = targetSystemProperties.getOperatingSystem();
            if (osType.isSetOperatingSystemType()) {
                // FIXME: somehow only one OS is ???
                String osName = osType.getOperatingSystemType().getOperatingSystemName().toString();
                availableResources.add(
                        new AvailableEnumResource(
                                StandardResources.osType.toString(),
                                StandardResources.osType.description(),
                                osName,
                                new String[]{osName}
                        )
                );
            }
        }
        if (targetSystemProperties.isSetProcessor()) {
            ProcessorType processor = targetSystemProperties.getProcessor();
            if (processor.isSetCPUArchitecture()) {
                CPUArchitectureType cpuArch = processor.getCPUArchitecture();
                String cpuArchName = cpuArch.getCPUArchitectureName().toString();
                availableResources.add(
                        new AvailableEnumResource(
                                StandardResources.cpuArchitecture.toString(),
                                StandardResources.cpuArchitecture.description(),
                                cpuArchName,
                                new String[]{cpuArchName}
                        )
                );
            }
            if (processor.isSetIndividualCPUSpeed()) {
                rangeValueTypeIntResources.put(StandardResources.individualCPUSpeed, processor.getIndividualCPUSpeed());
            }
        }


        if (targetSystemProperties.isSetIndividualCPUCount()) {
            rangeValueTypeIntResources.put(StandardResources.individualNumberOfCPUs, targetSystemProperties.getIndividualCPUCount());
        }
        if (targetSystemProperties.isSetIndividualCPUTime()) {
            rangeValueTypeIntResources.put(StandardResources.individualCPUTime, targetSystemProperties.getIndividualCPUTime());
        }
        if (targetSystemProperties.isSetIndividualPhysicalMemory()) {
            rangeValueTypeIntResources.put(StandardResources.individualPhysicalMemory, targetSystemProperties.getIndividualPhysicalMemory());
        }
        if (targetSystemProperties.isSetTotalCPUCount()) {
            rangeValueTypeIntResources.put(StandardResources.totalNumberOfCPUs, targetSystemProperties.getTotalCPUCount());
        }
        if (targetSystemProperties.isSetTotalResourceCount()) {
            rangeValueTypeIntResources.put(StandardResources.totalNumberOfNodes, targetSystemProperties.getTotalResourceCount());
        }

        availableResources.addAll(convertRangeValueTypesToIntResources(rangeValueTypeIntResources));

        return availableResources;
    }


    private static Collection<? extends AvailableResource> convertRangeValueTypesToIntResources(Map<StandardResources, RangeValueType> rangeValueTypeIntResources) {
        List<AvailableResource> availableResourceList = Lists.newArrayList();
        for (Map.Entry<StandardResources, RangeValueType> entry : rangeValueTypeIntResources.entrySet()) {
            String name = entry.getKey().toString();
            String description = entry.getKey().description();

            String defaultValue = "";
            if (entry.getValue().getExactArray().length > 0) {
                defaultValue = entry.getValue().getExactArray()[0].getStringValue();
            }

            int minValue = Integer.MIN_VALUE;
            int maxValue = Integer.MAX_VALUE;
            if (entry.getValue().getRangeArray().length > 0) {
                minValue = (int) entry.getValue().getRangeArray(0).getLowerBound().getDoubleValue();
                maxValue = (int) entry.getValue().getRangeArray(0).getUpperBound().getDoubleValue();
            }

            availableResourceList.add(new AvailableIntResource(name, description, defaultValue, minValue, maxValue));
        }
        return availableResourceList;
    }

    public static void mergeTargetSystemResources(Map<String, AvailableResource> availableResourceMap, List<AvailableResource> tssAvailableResources) {
        for (AvailableResource availableResource : tssAvailableResources) {
            String availableResourceKey = availableResource.getClass().getName() + "::" + availableResource.getName();
            AvailableResource existing = availableResourceMap.get(availableResourceKey);
            if (existing == null) {
                availableResourceMap.put(availableResourceKey, availableResource);
            } else {
                existing.mergeWith(availableResource);
            }
        }
    }
}
