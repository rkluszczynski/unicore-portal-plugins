package pl.plgrid.unicore.vasp.input;

import eu.unicore.util.configuration.ConfigurationException;
import eu.unicore.util.configuration.PropertiesHelper;
import eu.unicore.util.configuration.PropertyMD;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class VASPProperties extends PropertiesHelper {
    private static final String PREFIX = "vasp.input.";
    public static final Map<String, PropertyMD> META = new HashMap<String, PropertyMD>();
    public static final String MSG_ID = "vasp";

    static {
        META.put("incar", new PropertyMD("").setDescription("INCAR data source."));
        META.put("kpoints", new PropertyMD("").setDescription("KPOINTS data source."));
        META.put("poscar", new PropertyMD("").setDescription("POSCAR data source."));
        META.put("potcar", new PropertyMD("").setDescription("POTCAR data source."));
    }

    public VASPProperties(Properties properties) throws ConfigurationException {
        super(PREFIX, properties, META, logger);
    }

    private static final Logger logger = Logger.getLogger(VASPProperties.class);
}
