package edu.kit.informatik.pcc.service.data;

/**
 * Config class that holds all directories used.
 *
 * @author Josh Romanowski
 */
public class LocationConfig {

	// attributes

	/**
	 * Project directory
	 */
	private static final String PROJECT_DIR = System.getProperty("user.dir");
	/**
	 * Directory for the anonymized videos.
	 */
	public static final String ANONYM_VID_DIR = PROJECT_DIR + "\\vid";
	/**
	 * Directory for the metadata.
	 */
	public static final String META_DIR = PROJECT_DIR + "\\meta";
	/**
	 * Directory for all temporary data.
	 */
	public static final String TEMP_DIR = PROJECT_DIR + "\\tmp";
	/**
	 * Directory for project resources.
	 */
	public static final String RESOURCES_DIR = PROJECT_DIR + "\\src\\main\\resources";
	/**
	 * Directory for test resources.
	 */
	public static final String TEST_RESOURCES_DIR = PROJECT_DIR + "\\src\\test\\resources";
	/**
	 * Directory for output data.
	 */
	public static final String OUTPUT_DIR = PROJECT_DIR + "\\target";
}
