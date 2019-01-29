//
//   Copyright 2014-2019 Eduardo R. B. Marques
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//

package org.cooperari.core.aspectj;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.cooperari.CVersion;
import org.cooperari.config.CInstrument;
import org.cooperari.core.ACustomYieldPoint;
import org.cooperari.core.CustomYieldPoint;
import org.cooperari.core.util.IO;
import org.cooperari.errors.CInternalError;
import org.cooperari.feature.CAllFeatures;
import org.cooperari.feature.CFeature;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

//TODO: unit tests

/**
 * AspectJ weaving configuration.
 * 
 * <p>
 * An object of this type can be used to produce an XML file that is suitable
 * for setting up load-time weaving by AspectJ. [the format is described <a
 * href=
 * "https://eclipse.org/aspectj/doc/next/devguide/ltw-configuration.html">here
 * </a>].
 * </p>
 * 
 * @since 0.2
 *
 */
public final class WeavingConfiguration {
  /**
   * XML document handle.
   */
  private final Document _xmlDoc;

  /**
   * <code>&lt;aspectj&gt;</code> root element.
   */
  private final Element _rootElem;

  /**
   * <code>&lt;aspects&gt;</code> section element.
   */
  private final Element _aspectsElem;

  /**
   * <code>&lt;weaver&gt;</code> section element.
   */
  private final Element _weaverElem;

  /**
   * Constructs a new weaving configuration.
   * 
   * Initially, no aspects or weaver options are set.
   */
  public WeavingConfiguration() {
    _xmlDoc = DOC_FACTORY.newDocument();
    _rootElem = createNode("aspectj");
    _aspectsElem = createNode("aspects");
    _weaverElem = createNode("weaver");
    _rootElem.appendChild(_aspectsElem);
    _rootElem.appendChild(_weaverElem);
    _xmlDoc.appendChild(_rootElem);
    addWeaverOption("-verbose");
    addWeaverOption("-showWeaveInfo");
    addWeaverOption("-debug");
  }

  @SuppressWarnings("javadoc")
  private Element createNode(String tagName) {
    return _xmlDoc.createElement(tagName);
  }

  /**
   * Declare an aspect described by a class instance.
   * 
   * @param c Class for the aspect.
   */
  public void declareAspect(Class<?> c) {
    declareAspect(c.getCanonicalName());
  }

  /**
   * Declare an aspect identified by a class name.
   * <p>
   * The class name should be in a canonical format, i.e., it should be
   * formatted with the traditional "dot" notation, as returned by
   * {@link Class#getCanonicalName()}.
   * </p>
   * 
   * @param className Class name.
   */
  public void declareAspect(String className) {
    Element node = createNode("aspect");
    node.setAttribute("name", className);
    _aspectsElem.appendChild(node);
  }

  /**
   * Enable the use of aspects that match a given pattern.
   * 
   * @param pattern Matching pattern.
   */
  public void useAspect(String pattern) {
    configWithin(_aspectsElem, "include", pattern);
  }

  /**
   * Disable the use of aspects that match a given pattern.
   * 
   * @param pattern Matching pattern.
   */
  public void doNotUseAspect(String pattern) {
    configWithin(_aspectsElem, "exclude", pattern);
  }

  /**
   * Add an inline aspect.
   * @param abstractAspect Parent abstract aspect.
   * @param name Name of aspect.
   * @param pointcutName Pointcut expression.
   * @param pointcutExpr Pointcut expression.
   */
  public void addInlineAspect(Class<?> abstractAspect, String name, String pointcutName, String pointcutExpr) {
    Element caElem = _xmlDoc.createElement("concrete-aspect");
    caElem.setAttribute("name", name);
    caElem.setAttribute("extends", abstractAspect.getCanonicalName());
    Element pElem = _xmlDoc.createElement("pointcut");
    pElem.setAttribute("name", pointcutName);
    pElem.setAttribute("expression", pointcutExpr);
    caElem.appendChild(pElem);
    _aspectsElem.appendChild(caElem);
  }

  /**
   * Set the <code>options</code> XML attribute for the <code>&lt;weaver</code>
   * section element.
   * 
   * @param option Value for the attribute.
   */
  public void addWeaverOption(String option) {
    String options = _weaverElem.getAttribute("options");
    if (options.length() > 0) {
      options += ' ';
    }
    options += option;
    _weaverElem.setAttribute("options", options);
  }

  /**
   * Enable weaving for given class.
   * 
   * @param c Class object.
   */
  public void weave(Class<?> c) {
    weave(c.getCanonicalName());
  }

  /**
   * Enable weaving for classes that match a given pattern.
   * 
   * @param pattern Matching pattern.
   */
  public void weave(String pattern) {
    configWithin(_weaverElem, "include", pattern);
  }

  /**
   * Disable weaving for given class.
   * 
   * @param c Class object.
   */
  public void doNotWeave(Class<?> c) {
    doNotWeave(c.getCanonicalName());
  }

  /**
   * Disable weaving for classes that match a given pattern.
   * 
   * @param pattern Matching pattern.
   */
  public void doNotWeave(String pattern) {
    configWithin(_weaverElem, "exclude", pattern);
  }

  /**
   * Enable class dump before and after weaving for classes that match a given
   * pattern.
   * 
   * @param pattern Matching pattern.
   */
  public void enableDump(String pattern) {
    configWithin(_weaverElem, "dump", pattern);
  }

  /**
   * Enable class dump for all classes. The call is equivalent to
   * <code>enableDump("*")</code>.
   */
  public void enableDumpForAllClasses() {
    enableDump("*");
  }

  @SuppressWarnings("javadoc")
  private void configWithin(Element parent, String tagName, String value) {
    Element e = createNode(tagName);
    e.setAttribute("within", value);
    parent.appendChild(e);
  }

  /**
   * Save configuration to a file in XML format.
   * 
   * @param file File object.
   * @throws IOException If a file I/O error occurs.
   */
  public void saveAsXML(File file) throws IOException {
    FileOutputStream fos = new FileOutputStream(file);
    try {
      print(fos);
    } finally {
      fos.close();
    }
  }


  /**
   * Get the current configuration in DOM format. 
   * 
   * Note that a deep copy of the internal
   * DOM element is returned.
   * 
   * @return An {@link Element} object for the current configuration.
   */
  public Element getRootElement() {
    return (Element) _rootElem.cloneNode(true);
  }

  /**
   * Print configuration in XML format to an output stream.
   * 
   * @param os Output stream.
   */
  public void print(OutputStream os) {
    DOMSource ds = new DOMSource(_rootElem);
    try {
      StreamResult sr = new StreamResult(os);
      TRANSFORMER.transform(ds, sr);
    } catch (TransformerException e) {
      throw new CInternalError(e);
    }
  }

  /**
   * Get a weave configuration for a given class taking a
   * {@link CInstrument} instance as source configuration.
   * 
   * @param config An instance of {@link CInstrument}.
   * @param clazz Class subject to configuration.
   * @return A {@link WeavingConfiguration} object.
   */
  public static WeavingConfiguration create(CInstrument config, Class<?> clazz) {
    // Create configuration
    WeavingConfiguration wc = new WeavingConfiguration();
    // Handle aspects to use
    for (CFeature fh : CAllFeatures.getFeatures()) {
      Class<?> aspectClass = fh.getInstrumentationAspect();
      if (aspectClass != null) {
        if (aspectClass.getAnnotation(org.aspectj.lang.annotation.Aspect.class) == null) {
          throw new CInternalError("Aspect class " + aspectClass
              + " is not annotated with @Aspect");
        }
        wc.declareAspect(aspectClass);
      }
    }
    // Handle inline aspects 
    wc.declareAspect(ACustomYieldPoint.class);
    List<CustomYieldPoint> list = new ArrayList<>();
    for (CFeature fh : CAllFeatures.getFeatures()) {
       list.clear();
       fh.getCustomYieldPoints(list);
       for (CustomYieldPoint cyp : list) {
         wc.addInlineAspect(ACustomYieldPoint.class, cyp.getAspectName(), "yieldPoint", cyp.getPointcutExpression());
       }
    }
    // Handle weaving configuration
    if (config.selfWholePackage() && clazz.getPackage() != null) {
      wc.weave(clazz.getPackage().getName() + "..*");
    }

    if (config.self()) {
      wc.weave(clazz); 
    } 

    if (config.selfInnerClasses()) {
      wc.weave(clazz.getCanonicalName() + ".*");
    } 

    for (Class<?> c : config.classes()) {
      wc.weave(c);
    }
    for (String p : config.packages()) {
      wc.weave(p + ".*");
    }
    // Handle dump option
    if (config.ltwDump()) {
      wc.enableDumpForAllClasses();
    }
    // Handle options
    wc.addWeaverOption("-Xjoinpoints:synchronization");
    wc.addWeaverOption("-XmessageHandlerClass:" + AgentMessageHandler.class.getCanonicalName());

    if (config.ltwOptions().length() > 0 ) {
      for (String option : config.ltwOptions().split(" ")) {
        wc.addWeaverOption(option);
      }
    }
    return wc;
  }
  /**
   * Get a list of all declared aspect classes.
   * @return A list containing the declared aspect classes.
   */
  public List<Class<?>> getAspectClasses() {
    NodeList nodeList = _aspectsElem.getChildNodes();
    ArrayList<Class<?>> aspectList = new ArrayList<>();

    for (int index = 0; index < nodeList.getLength(); index++) {
      Element elem = (Element) nodeList.item(index);
      if (elem.getTagName().equals("aspect")) {
        String className = elem.getAttribute("name");
        try {
          aspectList.add(Class.forName(className));
        } catch (ClassNotFoundException e) {
          throw new CInternalError(e);
        }
      }
    }
    return aspectList;
  }

  /**
   * Save configuration to JAR file.
   * @param aspectsFile Compiled aspects JAR file (class files will be copied to the output file).
   * @param outputFile Output JAR file.
   * @throws IOException If a file I/O error occurs.
   */
  public void saveAsJAR(File aspectsFile, File outputFile) throws IOException {
    Manifest manifest = new Manifest();
    manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION,"1.0");
    manifest.getMainAttributes().putValue("Created-By", "Cooperari "+ CVersion.ID);
    JarFile jf = null;
    JarOutputStream jos = null;
    try { 
      jf = new JarFile(aspectsFile);
      Enumeration<JarEntry> jEntries = jf.entries();
      jos = new JarOutputStream(new FileOutputStream(outputFile), manifest);
      jos.putNextEntry(new JarEntry("META-INF/aop-ajc.xml"));
      print(jos);
      jos.closeEntry();
      while (jEntries.hasMoreElements()) {
        JarEntry entry = jEntries.nextElement();
        if (entry.getName().endsWith(".class")) {
          jos.putNextEntry(new JarEntry(entry));
          InputStream is = jf.getInputStream(entry);
          try {
            IO.bCopy(is, jos);
          } finally {
            is.close();
          }
          jos.closeEntry();
        }
      }
    } finally {
      if (jf != null) {
        jf.close();
      }
      if (jos != null) {
        jos.close();
      } 
    }
  }


  //-> The necessary factory and builder instances ...
  /**
   * Document builder factory handle.
   */
  private static final DocumentBuilder DOC_FACTORY;

  /**
   * Transformer handle.
   */
  private static final Transformer TRANSFORMER;

  static {
    try {
      DOC_FACTORY = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      TRANSFORMER = TransformerFactory.newInstance().newTransformer();
      TRANSFORMER.setOutputProperty(OutputKeys.INDENT, "yes");
      TRANSFORMER.setOutputProperty(OutputKeys.METHOD, "xml");
      TRANSFORMER.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
      TRANSFORMER.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
      TRANSFORMER.setOutputProperty(OutputKeys.STANDALONE, "yes");
      TRANSFORMER.setOutputProperty(OutputKeys.VERSION, "1.0");
    } catch (ParserConfigurationException | TransformerConfigurationException
        | TransformerFactoryConfigurationError e) {
      throw new CInternalError(e);
    }
  }





}
