/*
 * SonarQube PMD Plugin
 * Copyright (C) 2012 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.pmd;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Closeables;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.SourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.resources.InputFile;
import org.sonar.api.utils.SonarException;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;

public class PmdTemplate {
  private static final Logger LOG = LoggerFactory.getLogger(PmdTemplate.class);

  private static final Map<String, String> JAVA_VERSIONS = ImmutableMap.of(
    "1.1", "1.3",
    "1.2", "1.3",
    "5", "1.5",
    "6", "1.6",
    "7", "1.7");

  private final PMD pmd;

  public PmdTemplate(String javaVersion, ClassLoader classloader) {
    this(new PMD());
    setJavaVersion(pmd, javaVersion);
    setClassloader(pmd, classloader);
  }

  @VisibleForTesting
  PmdTemplate(PMD pmd) {
    this.pmd = pmd;
  }

  public void process(InputFile inputFile, Charset encoding, RuleSets rulesets, RuleContext ruleContext) {
    File file = inputFile.getFile();
    ruleContext.setSourceCodeFilename(file.getAbsolutePath());

    InputStream inputStream = null;
    try {
      inputStream = inputFile.getInputStream();

      pmd.processFile(inputStream, encoding.displayName(), rulesets, ruleContext);
    } catch (PMDException e) {
      LOG.error("Fail to execute PMD. Following file is ignored: " + file, e.getCause());
    } catch (Exception e) {
      LOG.error("Fail to execute PMD. Following file is ignored: " + file, e);
    } finally {
      Closeables.closeQuietly(inputStream);
    }
  }

  @VisibleForTesting
  static void setJavaVersion(PMD pmd, String javaVersion) {
    String version = normalize(javaVersion);
    SourceType sourceType = SourceType.getSourceTypeForId("java " + version);
    if (sourceType == null) {
      throw new SonarException("Unsupported Java version for PMD: " + version);
    }

    LOG.info("Java version: " + version);
    pmd.setJavaVersion(sourceType);
  }

  private static String normalize(String version) {
    return Functions.forMap(JAVA_VERSIONS, version).apply(version);
  }

  @VisibleForTesting
  static void setClassloader(PMD pmd, ClassLoader classloader) {
    pmd.setClassLoader(classloader);
  }

}
