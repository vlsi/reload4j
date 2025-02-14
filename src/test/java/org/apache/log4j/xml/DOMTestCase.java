/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.log4j.xml;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.VectorAppender;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableRenderer;
import org.apache.log4j.spi.OptionHandler;
import org.apache.log4j.spi.ThrowableRendererSupport;
import org.apache.log4j.util.Compare;
import org.apache.log4j.util.ControlFilter;
import org.apache.log4j.util.Filter;
import org.apache.log4j.util.ISO8601Filter;
import org.apache.log4j.util.LineNumberFilter;
import org.apache.log4j.util.Log4jAndNothingElseFilter;
import org.apache.log4j.util.SunReflectFilter;
import org.apache.log4j.util.Transformer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.log4j.TestContants.TEST_WITNESS_PREFIX;
import static org.apache.log4j.TestContants.TEST_INPUT_PREFIX;
import static org.apache.log4j.TestContants.TARGET_OUTPUT_PREFIX;

public class DOMTestCase {

  static String TEMP_A1 = TARGET_OUTPUT_PREFIX+"temp.A1";
  static String TEMP_A2 = TARGET_OUTPUT_PREFIX+"temp.A2";
  static String FILTERED_A1 = TARGET_OUTPUT_PREFIX+"filtered.A1";
  static String FILTERED_A2 = TARGET_OUTPUT_PREFIX+"filtered.A2";


  static String EXCEPTION1 = "java.lang.Exception: Just testing";
  static String EXCEPTION2 = "\\s*at .*\\(.*\\)";
  static String EXCEPTION3 = "\\s*at .*\\(Native Method\\)";
  static String EXCEPTION4 = "\\s*at .*\\(.*Compiled Code\\)";
  static String EXCEPTION5 = "\\s*at .*\\(.*libgcj.*\\)";


  static String TEST1_1A_PAT = 
                       "(TRACE|DEBUG|INFO |WARN |ERROR|FATAL) \\w*\\.\\w* - Message \\d";

  static String TEST1_1B_PAT = "(TRACE|DEBUG|INFO |WARN |ERROR|FATAL) root - Message \\d";

  static String TEST1_2_PAT = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2},\\d{3} "+
                        "\\[main]\\ (TRACE|DEBUG|INFO|WARN|ERROR|FATAL) .* - Message \\d";



  Logger root; 
  Logger logger;


  @Before
  public void setUp() {
    root = Logger.getRootLogger();
    logger = Logger.getLogger(DOMTestCase.class);
  }

  @After
  public void tearDown() {  
    root.getLoggerRepository().resetConfiguration();
  }

  @Test
  public void test1() throws Exception {
    DOMConfigurator.configure(TEST_INPUT_PREFIX+"xml/DOMTestCase1.xml");
    common();

    ControlFilter cf1 = new ControlFilter(new String[]{TEST1_1A_PAT, TEST1_1B_PAT, 
					       EXCEPTION1, EXCEPTION2, EXCEPTION3, EXCEPTION4, EXCEPTION5});

    ControlFilter cf2 = new ControlFilter(new String[]{TEST1_2_PAT, 
					       EXCEPTION1, EXCEPTION2, EXCEPTION3, EXCEPTION4, EXCEPTION5});

    Transformer.transform(
      TEMP_A1, FILTERED_A1,
      new Filter[] {
        cf1, new LineNumberFilter(), new SunReflectFilter(),
        new Log4jAndNothingElseFilter()
      });

    Transformer.transform(
      TEMP_A2, FILTERED_A2,
      new Filter[] {
        cf2, new LineNumberFilter(), new ISO8601Filter(),
        new Log4jAndNothingElseFilter()
      });

    assertTrue(Compare.compare(FILTERED_A1, TEST_WITNESS_PREFIX+"dom.A1.1"));
    assertTrue(Compare.compare(FILTERED_A2, TEST_WITNESS_PREFIX+"dom.A2.1"));
  }
  
  /**
   *   Tests processing of external entities in XML file.
   */

  @Test
  public void test4() throws Exception {
    DOMConfigurator.configure(TEST_INPUT_PREFIX+"xml/DOMTest4.xml");
    common();

    ControlFilter cf1 = new ControlFilter(new String[]{TEST1_1A_PAT, TEST1_1B_PAT, 
					       EXCEPTION1, EXCEPTION2, EXCEPTION3, EXCEPTION4, EXCEPTION5});

    ControlFilter cf2 = new ControlFilter(new String[]{TEST1_2_PAT, 
					       EXCEPTION1, EXCEPTION2, EXCEPTION3, EXCEPTION4, EXCEPTION5});

    Transformer.transform(
      TEMP_A1 + ".4", FILTERED_A1 + ".4",
      new Filter[] {
        cf1, new LineNumberFilter(), new Log4jAndNothingElseFilter()
      });

    Transformer.transform(
      TEMP_A2 + ".4", FILTERED_A2 + ".4",
      new Filter[] {
        cf2, new LineNumberFilter(), new ISO8601Filter(),
        new Log4jAndNothingElseFilter()
      });

    assertTrue(Compare.compare(FILTERED_A1 + ".4", TEST_WITNESS_PREFIX+"dom.A1.4"));
    assertTrue(Compare.compare(FILTERED_A2 + ".4", TEST_WITNESS_PREFIX+"dom.A2.4"));
  }

  void common() {
    String oldThreadName = Thread.currentThread().getName();
    Thread.currentThread().setName("main");

    int i = -1;
 
    logger.trace("Message " + ++i);
    root.trace("Message " + i);  
 
    logger.debug("Message " + ++i);
    root.debug("Message " + i);        

    logger.info ("Message " + ++i);
    root.info("Message " + i);        

    logger.warn ("Message " + ++i);
    root.warn("Message " + i);        

    logger.error("Message " + ++i);
    root.error("Message " + i);
    
    logger.log(Level.FATAL, "Message " + ++i);
    root.log(Level.FATAL, "Message " + i);    
    
    Exception e = new Exception("Just testing");
    logger.debug("Message " + ++i, e);
    root.debug("Message " + i, e);
    
    logger.error("Message " + ++i, e);
    root.error("Message " + i, e);    

    Thread.currentThread().setName(oldThreadName);
  }


    /**
     * CustomLogger implementation for testCategoryFactory1 and 2.
     */
  private static class CustomLogger extends Logger {
        /**
         * Creates new instance.
         * @param name logger name.
         */
      public CustomLogger(final String name) {
          super(name);
      }
  }

    /**
     * Creates new instances of CustomLogger.
     */
  public static class CustomLoggerFactory implements LoggerFactory {
        /**
         * Addivity, expected to be set false in configuration file.
         */
      private boolean additivity;

        /**
         * Create new instance of factory.
         */
      public CustomLoggerFactory() {
          additivity = true;
      }

        /**
         * Create new logger.
         * @param name logger name.
         * @return new logger.
         */
      public Logger makeNewLoggerInstance(final String name) {
          Logger logger = new CustomLogger(name);
          assertFalse(additivity);
          return logger;
      }

        /**
         * Set additivity.
         * @param newVal new value of additivity.
         */
      public void setAdditivity(final boolean newVal) {
          additivity = newVal;
      }
  }

    /**
     * CustomErrorHandler for testCategoryFactory2.
     */
  public static class CustomErrorHandler implements ErrorHandler {
      public CustomErrorHandler() {}
      public void activateOptions() {}
      public void setLogger(final Logger logger) {}
      public void error(String message, Exception e, int errorCode) {}
      public void error(String message) {}
      public void error(String message, Exception e, int errorCode, LoggingEvent event) {}
      public void setAppender(Appender appender) {}
      public void setBackupAppender(Appender appender) {}
  }

    /**
     * Tests that loggers mentioned in logger elements
     *    use the specified categoryFactory.  See bug 33708.
     */

  @Test
  public void testCategoryFactory1() {
      DOMConfigurator.configure(TEST_INPUT_PREFIX+"xml/categoryfactory1.xml");
      //
      //   logger explicitly mentioned in configuration,
      //         should be a CustomLogger
      Logger logger1 = Logger.getLogger("org.apache.log4j.xml.DOMTestCase.testCategoryFactory1.1");
      assertTrue(logger1 instanceof CustomLogger);
      //
      //   logger not explicitly mentioned in configuration,
      //         should use default factory
      Logger logger2 = Logger.getLogger("org.apache.log4j.xml.DOMTestCase.testCategoryFactory1.2");
      assertFalse(logger2 instanceof CustomLogger);
  }

    /**
     * Tests that loggers mentioned in logger-ref elements
     *    use the specified categoryFactory.  See bug 33708.
     */
    @Test
    public void testCategoryFactory2() {
        DOMConfigurator.configure(TEST_INPUT_PREFIX+"xml/categoryfactory2.xml");
        //
        //   logger explicitly mentioned in configuration,
        //         should be a CustomLogger
        Logger logger1 = Logger.getLogger("org.apache.log4j.xml.DOMTestCase.testCategoryFactory2.1");
        assertTrue(logger1 instanceof CustomLogger);
        //
        //   logger not explicitly mentioned in configuration,
        //         should use default factory
        Logger logger2 = Logger.getLogger("org.apache.log4j.xml.DOMTestCase.testCategoryFactory2.2");
        assertFalse(logger2 instanceof CustomLogger);
    }

    /**
     * Tests that loggers mentioned in logger elements
     *    use the specified loggerFactory.  See bug 33708.
     */

    @Test  
  public void testLoggerFactory1() {
      DOMConfigurator.configure(TEST_INPUT_PREFIX+"xml/loggerfactory1.xml");
      //
      //   logger explicitly mentioned in configuration,
      //         should be a CustomLogger
      Logger logger1 = Logger.getLogger("org.apache.log4j.xml.DOMTestCase.testLoggerFactory1.1");
      assertTrue(logger1 instanceof CustomLogger);
      //
      //   logger not explicitly mentioned in configuration,
      //         should use default factory
      Logger logger2 = Logger.getLogger("org.apache.log4j.xml.DOMTestCase.testLoggerFactory1.2");
      assertFalse(logger2 instanceof CustomLogger);
  }

    /**
     * Tests that reset="true" on log4j:configuration element resets
     * repository before configuration.
     * @throws Exception thrown on error.
     */

    @Test
  public void testReset() throws Exception {
      VectorAppender appender = new VectorAppender();
      appender.setName("V1");
      Logger.getRootLogger().addAppender(appender);
      DOMConfigurator.configure(TEST_INPUT_PREFIX+"xml/testReset.xml");
      assertNull(Logger.getRootLogger().getAppender("V1"));
  }


    /**
     * Test checks that configureAndWatch does initial configuration, see bug 33502.
      * @throws Exception if IO error.
     */

    @Test
  public void testConfigureAndWatch() throws Exception {
    DOMConfigurator.configureAndWatch(TEST_INPUT_PREFIX+"xml/DOMTestCase1.xml");
    assertNotNull(Logger.getRootLogger().getAppender("A1"));
  }


    /**
     * This test checks that the subst method of an extending class
     * is checked when evaluating parameters.  See bug 43325.
     *
     */

    @Test
  public void testOverrideSubst() {
      DOMConfigurator configurator = new DOMConfigurator() {
          protected String subst(final String value) {
              if ((TARGET_OUTPUT_PREFIX+"temp.A1").equals(value)) {
                  return TARGET_OUTPUT_PREFIX+"subst-test.A1";
              }
              return value;
          }
      };
      configurator.doConfigure(TEST_INPUT_PREFIX+"xml/DOMTestCase1.xml", LogManager.getLoggerRepository());
      FileAppender a1 = (FileAppender) Logger.getRootLogger().getAppender("A1");
      String file = a1.getFile();
      assertEquals(TARGET_OUTPUT_PREFIX+"subst-test.A1", file);
  }

    /**
     * Mock ThrowableRenderer for testThrowableRenderer.  See bug 45721.
     */
    public static class MockThrowableRenderer implements ThrowableRenderer, OptionHandler {
        private boolean activated = false;
        private boolean showVersion = true;

        public MockThrowableRenderer() {
        }

        public void activateOptions() {
            activated = true;
        }

        public boolean isActivated() {
            return activated;
        }

        public String[] doRender(final Throwable t) {
            return new String[0];
        }

        public void setShowVersion(boolean v) {
            showVersion = v;
        }

        public boolean getShowVersion() {
            return showVersion;
        }
    }

    /**
     * Test of log4j.throwableRenderer support.  See bug 45721.
     */
    @Test
    public void testThrowableRenderer1() {
        DOMConfigurator.configure(TEST_INPUT_PREFIX+"xml/throwableRenderer1.xml");
        ThrowableRendererSupport repo = (ThrowableRendererSupport) LogManager.getLoggerRepository();
        MockThrowableRenderer renderer = (MockThrowableRenderer) repo.getThrowableRenderer();
        LogManager.resetConfiguration();
        assertNotNull(renderer);
        assertEquals(true, renderer.isActivated());
        assertEquals(false, renderer.getShowVersion());
    }

    /**
     * Test for bug 47465.
     * configure(URL) did not close opened JarURLConnection.
     * @throws IOException if IOException creating properties jar.
     */
    @Test
    public void testJarURL() throws IOException {
        File input = new File(TEST_INPUT_PREFIX+"xml/defaultInit.xml");
        System.out.println(input.getAbsolutePath());
        InputStream is = new FileInputStream(input);
        File dir = new File("output");
        dir.mkdirs();
        File file = new File(TARGET_OUTPUT_PREFIX+"xml.jar");
        ZipOutputStream zos =
            new ZipOutputStream(new FileOutputStream(file));
        zos.putNextEntry(new ZipEntry("log4j.xml"));
        int len;
        byte[] buf = new byte[1024];
        while ((len = is.read(buf)) > 0) {
            zos.write(buf, 0, len);
        }
        zos.closeEntry();
        zos.close();
        URL url = new URL("jar:" + file.toURL() + "!/log4j.xml");
        DOMConfigurator.configure(url);
        assertTrue(file.delete());
        assertFalse(file.exists());
    }

}
