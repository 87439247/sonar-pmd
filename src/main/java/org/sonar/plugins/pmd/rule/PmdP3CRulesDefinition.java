/*
 * SonarQube PMD Plugin
 * Copyright (C) 2012-2018 SonarSource SA
 * mailto:info AT sonarsource DOT com
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
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.plugins.pmd.rule;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.plugins.pmd.PmdConstants;

public final class PmdP3CRulesDefinition implements RulesDefinition {

    public PmdP3CRulesDefinition() {
        // Do nothing
    }

    @Override
    public void define(Context context) {

        NewRepository repository = context
                .createRepository(PmdConstants.P3C_REPOSITORY_KEY, PmdConstants.LANGUAGE_KEY)
                .setName(PmdConstants.P3C_REPOSITORY_NAME);

        PmdRulesDefinition.extractRulesData(repository, "/org/sonar/plugins/pmd/rules-p3c.xml", "/org/sonar/l10n/pmd/rules/pmd-p3c");

        repository.done();
    }
}