package com.cabsoft.pdf.form.utils;

import java.io.File;

/**
 * 이 클래스는 시스템 속성 정보를 제공합니다.
 */
@SuppressWarnings("javadoc")
public class SystemUtils {

    /**
     *
     */
    public static final String FILE_ENCODING = getSystemProperty("file.encoding");
    /**
     *
     */
    public static final String FILE_SEPARATOR = getSystemProperty("file.separator");
    /**
     *
     */
    public static final String LINE_SEPARATOR = getSystemProperty("line.separator");
    /**
     *
     */
    public static final String PATH_SEPARATOR = getSystemProperty("path.separator");
    /**
     *
     */
    public static final String JAVA_AWT_HEADLESS = getSystemProperty("java.awt.headless");
    /**
     *
     */
    public static final String JAVA_CLASS_PATH = getSystemProperty("java.class.path");
    /**
     *
     */
    public static final String JAVA_CLASS_VERSION = getSystemProperty("java.class.version");
    /**
     *
     */
    public static final String JAVA_HOME = getSystemProperty("java.home");
    /**
     *
     */
    public static final String JAVA_IO_TMPDIR = getSystemProperty("java.io.tmpdir");
    /**
     *
     */
    public static final String JAVA_VERSION = getSystemProperty("java.version");
    /**
     *
     */
    public static final String OS_NAME = getSystemProperty("os.name");
    /**
     *
     */
    public static final String OS_VERSION = getSystemProperty("os.version");
    /**
     *
     */
    public static final String SERVLET_VERSION = getServletVersion();
    /**
     *
     */
    public static final String USER_HOME = getSystemProperty("user.home");
    /**
     *
     */
    public static final String USER_DIR = getSystemProperty("user.dir");
    /**
     *
     */
    public static final String OS_NAME_WINDOWS_PREFIX = "Windows";
    /**
     *
     */
    public static final boolean IS_OS_WINDOWS = isOS(OS_NAME_WINDOWS_PREFIX);
    /**
     *
     */
    public static final boolean IS_OS_AIX = isOS("AIX");
    /**
     *
     */
    public static final boolean IS_OS_HP_UX = isOS("HP-UX");
    /**
     *
     */
    public static final boolean IS_OS_LINUX = isOS("Linux") || isOS("LINUX");
    /**
     *
     */
    public static final boolean IS_OS_MAC = isOS("Mac");
    /**
     *
     */
    public static final boolean IS_OS_MAC_OSX = isOS("Mac OS X");
    /**
     *
     */
    public static final boolean IS_OS_OS2 = isOS("OS/2");
    /**
     *
     */
    public static final boolean IS_OS_SOLARIS = isOS("Solaris");
    /**
     *
     */
    public static final boolean IS_OS_SUN_OS = isOS("SunOS");
    /**
     *
     */
    public static final boolean IS_OS_UNIX = IS_OS_AIX || IS_OS_HP_UX || IS_OS_LINUX || IS_OS_MAC_OSX || IS_OS_SOLARIS || IS_OS_SUN_OS;

    /**
     * <p>키(key)에 해당하는 시스템 속성을 가져온다.</p>
     *
     * @param key
     * @return
     * @throws SecurityException
     */
	public static String getSystemProperty(String key) throws SecurityException {
        return System.getProperty(key);
    }

    /**
     * <p>키(key)에 해당하는 시스템 속성을 가져온다.</p>
     *
     * @param key
     * @param defaultValue 기본값
     * @return
     * @throws SecurityException
     */
    public static String getSystemProperty(String key, String defaultValue) throws SecurityException {
        return System.getProperty(key, defaultValue);
    }

    /**
     * <p>해당 OS인지 판단한다.</p>
     *
     * @param osNamePrefix OS 이름 접두사
     * @return
     */
    public static boolean isOS(String osNamePrefix) {
        return StringUtils.startsWith(OS_NAME, osNamePrefix);
    }

    /**
     * <p>해당 OS인지 판단한다.</p>
     *
     * @param osNamePrefix OS 이름 접두사
     * @param osVersionPrefix OS 버젼 접두사
     * @return
     */
    public static boolean isOS(String osNamePrefix, String osVersionPrefix) {
        return StringUtils.startsWith(OS_NAME, osNamePrefix) && StringUtils.startsWith(OS_VERSION, osVersionPrefix);
    }

    /**
     * <p>java.home 디렉토리를 반환한다.</p>
     * @return
     */
    public static File getJavaHome() {
        return new File(JAVA_HOME);
    }

    /**
     * <p>java.io.tmpdir 디렉토리를 반환한다.</p>
     * @return
     */
    public static File getJavaIoTmpDir() {
        return new File(JAVA_IO_TMPDIR);
    }

    /**
     * <p>user.home 디렉토리를 반환한다.</p>
     *
     * @return
     */
    public static File getUserHome() {
        return new File(USER_HOME);
    }

    /**
     * <p>java.dir 디렉토리를 반환한다.</p>
     *
     * @return
     */
    public static File getUserDir() {
        return new File(USER_DIR);
    }

    /**
     *
     * @return
     */
    public static String getServletVersion() {
        /*******************************************************************************
         * Determine the servlet version by looking at available classes
         * and variables
         * javax.servlet.http.HttpSession was introduced in Servlet API 2.0
         * javax.servlet.RequestDispatcher was introduced in Servlet API 2.1
         * javax.servlet.http.HttpServletResponse.SC_EXPECTATION_FAILED was
         * introduced in Servlet API 2.2
         * javax.servlet.Filter is slated to be introduced in Servlet API 2.3
         * Count up versions until a NoClassDefFoundError or NoSuchFieldException
         * ends the try
         *******************************************************************************/
        String ver = null;
        try {
            ver = "1.0";
            Class.forName("javax.servlet.http.HttpSession");
            ver = "2.0";
            Class.forName("javax.servlet.RequestDispatcher");
            ver = "2.1";
            Class.forName("javax.servlet.http.HttpServletResponse").getDeclaredField("SC_EXPECTATION_FAILED");
            ver = "2.2";
            Class.forName("javax.servlet.Filter");
            ver = "2.3";
        } catch (Throwable t) {
        }
        return ver;
    }

    /**
     *
     * @param src
     * @return
     */
    public static String getPathWithOS(String src) {
        if (SystemUtils.IS_OS_WINDOWS) {
            return StringUtils.replaceAll(src, "/", System.getProperty("file.separator"));
        } else {
            return StringUtils.replaceAll(src, "\\", System.getProperty("file.separator"));
        }
    }

    /**
     *
     * @param src
     * @return
     */
    public static String replaceSystemPathString(String src){
        String ret = "";
        if(StringUtils.isEmpty(src)){
            ret = "";
        }else{
            ret = StringUtils.replaceAll(src, "\\", FILE_SEPARATOR);
            ret = StringUtils.replaceAll(ret, "/", FILE_SEPARATOR);
        }
        return ret;
    }

}
