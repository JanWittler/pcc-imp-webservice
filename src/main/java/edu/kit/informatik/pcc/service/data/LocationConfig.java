package edu.kit.informatik.pcc.service.data;

import java.io.File;

/**
 * Config class that holds all directories used.
 *
 * @author Josh Romanowski and David Laubenstein
 *         Created by David Laubenstein on 01/18/2017
 */
public class LocationConfig {

    /* #############################################################################################
     *                                  attributes
     * ###########################################################################################*/

    /**
     * Project directory
     */
    public static final String PROJECT_DIR = System.getProperty("user.dir");
    /**
     * Directory for the anonymized videos.
     */
    public static final String ANONYM_VID_DIR = PROJECT_DIR + File.separator + "vid";
    /**
     * Directory for log files.
     */
    public static final String LOG_DIR = PROJECT_DIR + File.separator + "log";
    /**
     * Directory for test resources.
     */
    public static final String TEST_RESOURCES_DIR = PROJECT_DIR + File.separator + "src" + File.separator + "test" + File.separator + "resources";
    /**
     * Directory for output data.
     */
    public static final String OUTPUT_DIR = PROJECT_DIR + File.separator + "target";
}
