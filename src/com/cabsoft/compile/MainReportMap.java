package com.cabsoft.compile;

public class MainReportMap {
	private final String separator = System.getProperty("file.separator");
	private String ReportPath = null;
	private String FullName = null;
	private Long VersionTimes = null;
	private String Name = null;
	private boolean IsCompiled = false;

	/**
	 * @return the reportPath
	 */
	public String getReportPath() {
		return ReportPath;
	}

	/**
	 * @param reportPath
	 *            the reportPath to set
	 */
	public void setReportPath(String reportPath) {
		ReportPath = reportPath;
	}

	/**
	 * @return the fullName
	 */
	public String getFullName() {
		return FullName;
	}

	/**
	 * @param fullName
	 *            the fullName to set
	 */
	public void setFullName(String fullName) {
		FullName = fullName;
	}

	/**
	 * @return the versionTimes
	 */
	public Long getVersionTimes() {
		return VersionTimes;
	}

	/**
	 * @param versionTimes
	 *            the versionTimes to set
	 */
	public void setVersionTimes(Long versionTimes) {
		VersionTimes = versionTimes;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return Name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		Name = name;
	}

	/**
	 * @return the isCompiled
	 */
	public boolean getIsCompiled() {
		return IsCompiled;
	}

	/**
	 * @param isCompiled
	 *            the isCompiled to set
	 */
	public void setIsCompiled(boolean isCompiled) {
		IsCompiled = isCompiled;
	}

	/**
	 * @return the separator
	 */
	public String getSeparator() {
		return separator;
	}
}
