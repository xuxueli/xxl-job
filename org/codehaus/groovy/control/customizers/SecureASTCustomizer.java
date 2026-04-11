/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.codehaus.groovy.control.customizers;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.CodeVisitorSupport;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.ImportNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ArrayExpression;
import org.codehaus.groovy.ast.expr.AttributeExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.BitwiseNegationExpression;
import org.codehaus.groovy.ast.expr.BooleanExpression;
import org.codehaus.groovy.ast.expr.CastExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ClosureListExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.expr.ElvisOperatorExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.FieldExpression;
import org.codehaus.groovy.ast.expr.GStringExpression;
import org.codehaus.groovy.ast.expr.LambdaExpression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.MethodPointerExpression;
import org.codehaus.groovy.ast.expr.MethodReferenceExpression;
import org.codehaus.groovy.ast.expr.NotExpression;
import org.codehaus.groovy.ast.expr.PostfixExpression;
import org.codehaus.groovy.ast.expr.PrefixExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.RangeExpression;
import org.codehaus.groovy.ast.expr.SpreadExpression;
import org.codehaus.groovy.ast.expr.SpreadMapExpression;
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;
import org.codehaus.groovy.ast.expr.TernaryExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.codehaus.groovy.ast.expr.UnaryMinusExpression;
import org.codehaus.groovy.ast.expr.UnaryPlusExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.AssertStatement;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.BreakStatement;
import org.codehaus.groovy.ast.stmt.CaseStatement;
import org.codehaus.groovy.ast.stmt.CatchStatement;
import org.codehaus.groovy.ast.stmt.ContinueStatement;
import org.codehaus.groovy.ast.stmt.DoWhileStatement;
import org.codehaus.groovy.ast.stmt.EmptyStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.ForStatement;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.stmt.SwitchStatement;
import org.codehaus.groovy.ast.stmt.SynchronizedStatement;
import org.codehaus.groovy.ast.stmt.ThrowStatement;
import org.codehaus.groovy.ast.stmt.TryCatchStatement;
import org.codehaus.groovy.ast.stmt.WhileStatement;
import org.codehaus.groovy.classgen.BytecodeExpression;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.syntax.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This customizer allows securing source code by controlling what code constructs are permitted.
 * This is typically done when using Groovy for its scripting or domain specific language (DSL) features.
 * For example, if you only want to allow arithmetic operations in a groovy shell,
 * you can configure this customizer to restrict package imports, method calls and so on.
 * <p>
 * Most of the security customization options found in this class work with either <i>allowed</i> or <i>disallowed</i> lists.
 * This means that, for a single option, you can set an allowed list OR a disallowed list, but not both.
 * You can mix allowed/disallowed strategies for different options.
 * For example, you can have an allowed import list and a disallowed tokens list.
 * <p>
 * The recommended way of securing shells is to use allowed lists because it is guaranteed that future features of the
 * Groovy language won't be accidentally allowed unless explicitly added to the allowed list.
 * Using disallowed lists, you can limit the features of the language constructs supported by your shell by opting
 * out, but new language features are then implicitly also available and this may not be desirable.
 * The implication is that you might need to update your configuration with each new release.
 * <p>
 * If neither an allowed list nor a disallowed list is set, then everything is permitted.
 * <p>
 * Combinations of import and star import constraints are authorized as long as you use the same type of list for both.
 * For example, you may use an import allowed list and a star import allowed list together, but you cannot use an import
 * allowed list with a star import disallowed list. Static imports are handled separately, meaning that disallowing an
 * import <b>does not</b> prevent from allowing a static import.
 * <p>
 * Eventually, if the features provided here are not sufficient, you may implement custom AST filtering handlers, either
 * implementing the {@link StatementChecker} interface or {@link ExpressionChecker} interface then register your
 * handlers thanks to the {@link #addExpressionCheckers(ExpressionChecker...)}
 * and {@link #addStatementCheckers(StatementChecker...)}
 * methods.
 * <p>
 * Here is an example of usage. We will create a groovy classloader which only supports arithmetic operations and imports
 * the {@code java.lang.Math} classes by default.
 *
 * <pre>
 * final ImportCustomizer imports = new ImportCustomizer().addStaticStars('java.lang.Math') // add static import of java.lang.Math
 * final SecureASTCustomizer secure = new SecureASTCustomizer()
 * secure.with {
 *     closuresAllowed = false
 *     methodDefinitionAllowed = false
 *
 *     allowedImports = []
 *     allowedStaticImports = []
 *     allowedStaticStarImports = ['java.lang.Math'] // only java.lang.Math is allowed
 *
 *     allowedTokens = [
 *             PLUS,
 *             MINUS,
 *             MULTIPLY,
 *             DIVIDE,
 *             REMAINDER,
 *             POWER,
 *             PLUS_PLUS,
 *             MINUS_MINUS,
 *             COMPARE_EQUAL,
 *             COMPARE_NOT_EQUAL,
 *             COMPARE_LESS_THAN,
 *             COMPARE_LESS_THAN_EQUAL,
 *             COMPARE_GREATER_THAN,
 *             COMPARE_GREATER_THAN_EQUAL,
 *     ].asImmutable()
 *
 *     allowedConstantTypesClasses = [
 *             Integer,
 *             Float,
 *             Long,
 *             Double,
 *             BigDecimal,
 *             Integer.TYPE,
 *             Long.TYPE,
 *             Float.TYPE,
 *             Double.TYPE
 *     ].asImmutable()
 *
 *     allowedReceiversClasses = [
 *             Math,
 *             Integer,
 *             Float,
 *             Double,
 *             Long,
 *             BigDecimal
 *     ].asImmutable()
 * }
 * CompilerConfiguration config = new CompilerConfiguration()
 * config.addCompilationCustomizers(imports, secure)
 * GroovyClassLoader loader = new GroovyClassLoader(this.class.classLoader, config)
 *  </pre>
 * <p>
 *  Note: {@code SecureASTCustomizer} allows you to lock down the grammar of scripts but by itself isn't intended
 *  to be the complete solution of all security issues when running scripts on the JVM. You might also want to
 *  consider setting the {@code groovy.grape.enable} System property to false, augmenting use of the customizer
 *  with additional techniques, and following standard security principles for JVM applications.
 *  <p>
 *  For more information, please read:
 *  <li><a href="https://melix.github.io/blog/2015/03/sandboxing.html">Improved sandboxing of Groovy scripts</a></li>
 *  <li><a href="https://www.oracle.com/java/technologies/javase/seccodeguide.html">Oracle's Secure Coding Guidelines</a></li>
 *  <li><a href="https://snyk.io/blog/10-java-security-best-practices/">10 Java security best practices</a></li>
 *  <li><a href="https://www.infoworld.com/article/2076837/twelve-rules-for-developing-more-secure-java-code.html">Thirteen rules for developing secure Java applications</a></li>
 *  <li><a href="https://www.guardrails.io/blog/12-java-security-best-practices/">12 Java Security Best Practices</a></li>
 *
 * @since 1.8.0
 */
public class SecureASTCustomizer extends CompilationCustomizer {

    private boolean isPackageAllowed = true;
    private boolean isClosuresAllowed = true;
    private boolean isMethodDefinitionAllowed = true;

    // imports
    private List<String> allowedImports;
    private List<String> disallowedImports;

    // static imports
    private List<String> allowedStaticImports;
    private List<String> disallowedStaticImports;

    // star imports
    private List<String> allowedStarImports;
    private List<String> disallowedStarImports;

    // static star imports
    private List<String> allowedStaticStarImports;
    private List<String> disallowedStaticStarImports;

    // indirect import checks
    // if set to true, then security rules on imports will also be applied on classnodes.
    // Direct instantiation of classes without imports will therefore also fail if this option is enabled
    private boolean isIndirectImportCheckEnabled;

    // statements
    private List<Class<? extends Statement>> allowedStatements;
    private List<Class<? extends Statement>> disallowedStatements;
    private final List<StatementChecker> statementCheckers = new LinkedList<>();

    // expressions
    private List<Class<? extends Expression>> allowedExpressions;
    private List<Class<? extends Expression>> disallowedExpressions;
    private final List<ExpressionChecker> expressionCheckers = new LinkedList<>();

    // tokens from Types
    private List<Integer> allowedTokens;
    private List<Integer> disallowedTokens;

    // constant types
    private List<String> allowedConstantTypes;
    private List<String> disallowedConstantTypes;

    // receivers
    private List<String> allowedReceivers;
    private List<String> disallowedReceivers;

    public SecureASTCustomizer() {
        super(CompilePhase.CANONICALIZATION);
    }

    public boolean isMethodDefinitionAllowed() {
        return isMethodDefinitionAllowed;
    }

    public void setMethodDefinitionAllowed(final boolean methodDefinitionAllowed) {
        isMethodDefinitionAllowed = methodDefinitionAllowed;
    }

    public boolean isPackageAllowed() {
        return isPackageAllowed;
    }

    public boolean isClosuresAllowed() {
        return isClosuresAllowed;
    }

    public void setClosuresAllowed(final boolean closuresAllowed) {
        isClosuresAllowed = closuresAllowed;
    }

    public void setPackageAllowed(final boolean packageAllowed) {
        isPackageAllowed = packageAllowed;
    }

    public List<String> getDisallowedImports() {
        return disallowedImports;
    }

    /**
     * Legacy alias for {@link #getDisallowedImports()}
     */
    @Deprecated
    public List<String> getImportsBlacklist() {
        return getDisallowedImports();
    }

    public void setDisallowedImports(final List<String> disallowedImports) {
        if (allowedImports != null || allowedStarImports != null) {
            throw new IllegalArgumentException("You are not allowed to set both an allowed list and a disallowed list");
        }
        this.disallowedImports = disallowedImports;
    }

    /**
     * Legacy alias for {@link #setDisallowedImports(List)}
     */
    @Deprecated
    public void setImportsBlacklist(final List<String> disallowedImports) {
        setDisallowedImports(disallowedImports);
    }

    public List<String> getAllowedImports() {
        return allowedImports;
    }

    /**
     * Legacy alias for {@link #getAllowedImports()}
     */
    @Deprecated
    public List<String> getImportsWhitelist() {
        return getAllowedImports();
    }

    public void setAllowedImports(final List<String> allowedImports) {
        if (disallowedImports != null || disallowedStarImports != null) {
            throw new IllegalArgumentException("You are not allowed to set both an allowed list and a disallowed list");
        }
        this.allowedImports = allowedImports;
    }

    /**
     * Legacy alias for {@link #setAllowedImports(List)}
     */
    @Deprecated
    public void setImportsWhitelist(final List<String> allowedImports) {
        setAllowedImports(allowedImports);
    }

    public List<String> getDisallowedStarImports() {
        return disallowedStarImports;
    }

    /**
     * Legacy alias for {@link #getDisallowedStarImports()}
     */
    @Deprecated
    public List<String> getStarImportsBlacklist() {
        return getDisallowedStarImports();
    }

    public void setDisallowedStarImports(final List<String> disallowedStarImports) {
        if (allowedImports != null || allowedStarImports != null) {
            throw new IllegalArgumentException("You are not allowed to set both an allowed list and a disallowed list");
        }
        this.disallowedStarImports = normalizeStarImports(disallowedStarImports);
        if (this.disallowedImports == null) disallowedImports = Collections.emptyList();
    }

    /**
     * Legacy alias for {@link #setDisallowedStarImports(List)}
     */
    @Deprecated
    public void setStarImportsBlacklist(final List<String> disallowedStarImports) {
        setDisallowedStarImports(disallowedStarImports);
    }

    public List<String> getAllowedStarImports() {
        return allowedStarImports;
    }

    /**
     * Legacy alias for {@link #getAllowedStarImports()}
     */
    @Deprecated
    public List<String> getStarImportsWhitelist() {
        return getAllowedStarImports();
    }

    public void setAllowedStarImports(final List<String> allowedStarImports) {
        if (disallowedImports != null || disallowedStarImports != null) {
            throw new IllegalArgumentException("You are not allowed to set both an allowed list and a disallowed list");
        }
        this.allowedStarImports = normalizeStarImports(allowedStarImports);
        if (this.allowedImports == null) allowedImports = Collections.emptyList();
    }

    /**
     * Legacy alias for {@link #setAllowedStarImports(List)}
     */
    @Deprecated
    public void setStarImportsWhitelist(final List<String> allowedStarImports) {
        setAllowedStarImports(allowedStarImports);
    }

    private static List<String> normalizeStarImports(List<String> starImports) {
        List<String> result = new ArrayList<>(starImports.size());
        for (String starImport : starImports) {
            if (starImport.endsWith(".*")) {
                result.add(starImport);
            } else if (starImport.endsWith("**")) {
                result.add(starImport.replaceFirst("\\*+$", ""));
            } else if (starImport.endsWith(".")) {
                result.add(starImport + "*");
            } else {
                result.add(starImport + ".*");
            }
        }
        return Collections.unmodifiableList(result);
    }

    public List<String> getDisallowedStaticImports() {
        return disallowedStaticImports;
    }

    /**
     * Legacy alias for {@link #getDisallowedStaticImports()}
     */
    @Deprecated
    public List<String> getStaticImportsBlacklist() {
        return getDisallowedStaticImports();
    }

    public void setDisallowedStaticImports(final List<String> disallowedStaticImports) {
        if (allowedStaticImports != null || allowedStaticStarImports != null) {
            throw new IllegalArgumentException("You are not allowed to set both an allowed list and a disallowed list");
        }
        this.disallowedStaticImports = disallowedStaticImports;
    }

    /**
     * Legacy alias for {@link #setDisallowedStaticImports(List)}
     */
    @Deprecated
    public void setStaticImportsBlacklist(final List<String> disallowedStaticImports) {
        setDisallowedStaticImports(disallowedStaticImports);
    }

    public List<String> getAllowedStaticImports() {
        return allowedStaticImports;
    }

    /**
     * Legacy alias for {@link #getAllowedStaticImports()}
     */
    @Deprecated
    public List<String> getStaticImportsWhitelist() {
        return getAllowedStaticImports();
    }

    public void setAllowedStaticImports(final List<String> allowedStaticImports) {
        if (disallowedStaticImports != null || disallowedStaticStarImports != null) {
            throw new IllegalArgumentException("You are not allowed to set both an allowed list and a disallowed list");
        }
        this.allowedStaticImports = allowedStaticImports;
    }

    /**
     * Legacy alias for {@link #setAllowedStaticImports(List)}
     */
    @Deprecated
    public void setStaticImportsWhitelist(final List<String> allowedStaticImports) {
        setAllowedStaticImports(allowedStaticImports);
    }

    public List<String> getDisallowedStaticStarImports() {
        return disallowedStaticStarImports;
    }

    /**
     * Legacy alias for {@link #getDisallowedStaticStarImports()}
     */
    @Deprecated
    public List<String> getStaticStarImportsBlacklist() {
        return getDisallowedStaticStarImports();
    }

    public void setDisallowedStaticStarImports(final List<String> disallowedStaticStarImports) {
        if (allowedStaticImports != null || allowedStaticStarImports != null) {
            throw new IllegalArgumentException("You are not allowed to set both an allowed list and a disallowed list");
        }
        this.disallowedStaticStarImports = normalizeStarImports(disallowedStaticStarImports);
        if (this.disallowedStaticImports == null) this.disallowedStaticImports = Collections.emptyList();
    }

    /**
     * Legacy alias for {@link #setDisallowedStaticStarImports(List)}
     */
    @Deprecated
    public void setStaticStarImportsBlacklist(final List<String> disallowedStaticStarImports) {
        setDisallowedStaticStarImports(disallowedStaticStarImports);
    }

    public List<String> getAllowedStaticStarImports() {
        return allowedStaticStarImports;
    }

    /**
     * Legacy alias for {@link #getAllowedStaticStarImports()}
     */
    @Deprecated
    public List<String> getStaticStarImportsWhitelist() {
        return getAllowedStaticStarImports();
    }

    public void setAllowedStaticStarImports(final List<String> allowedStaticStarImports) {
        if (disallowedStaticImports != null || disallowedStaticStarImports != null) {
            throw new IllegalArgumentException("You are not allowed to set both an allowed list and a disallowed list");
        }
        this.allowedStaticStarImports = normalizeStarImports(allowedStaticStarImports);
        if (this.allowedStaticImports == null) this.allowedStaticImports = Collections.emptyList();
    }

    /**
     * Legacy alias for {@link #setAllowedStaticStarImports(List)}
     */
    @Deprecated
    public void setStaticStarImportsWhitelist(final List<String> allowedStaticStarImports) {
        setAllowedStaticStarImports(allowedStaticStarImports);
    }

    public List<Class<? extends Expression>> getDisallowedExpressions() {
        return disallowedExpressions;
    }

    /**
     * Legacy alias for {@link #getDisallowedExpressions()}
     */
    @Deprecated
    public List<Class<? extends Expression>> getExpressionsBlacklist() {
        return getDisallowedExpressions();
    }

    public void setDisallowedExpressions(final List<Class<? extends Expression>> disallowedExpressions) {
        if (allowedExpressions != null) {
            throw new IllegalArgumentException("You are not allowed to set both an allowed list and a disallowed list");
        }
        this.disallowedExpressions = disallowedExpressions;
    }

    /**
     * Legacy alias for {@link #setDisallowedExpressions(List)}
     */
    @Deprecated
    public void setExpressionsBlacklist(final List<Class<? extends Expression>> disallowedExpressions) {
        setDisallowedExpressions(disallowedExpressions);
    }

    public List<Class<? extends Expression>> getAllowedExpressions() {
        return allowedExpressions;
    }

    /**
     * Legacy alias for {@link #getAllowedExpressions()}
     */
    @Deprecated
    public List<Class<? extends Expression>> getExpressionsWhitelist() {
        return getAllowedExpressions();
    }

    public void setAllowedExpressions(final List<Class<? extends Expression>> allowedExpressions) {
        if (disallowedExpressions != null) {
            throw new IllegalArgumentException("You are not allowed to set both an allowed list and a disallowed list");
        }
        this.allowedExpressions = allowedExpressions;
    }

    /**
     * Legacy alias for {@link #setAllowedExpressions(List)}
     */
    @Deprecated
    public void setExpressionsWhitelist(final List<Class<? extends Expression>> allowedExpressions) {
        setAllowedExpressions(allowedExpressions);
    }

    public List<Class<? extends Statement>> getDisallowedStatements() {
        return disallowedStatements;
    }

    /**
     * Legacy alias for {@link #getDisallowedStatements()}
     */
    @Deprecated
    public List<Class<? extends Statement>> getStatementsBlacklist() {
        return getDisallowedStatements();
    }

    public void setDisallowedStatements(final List<Class<? extends Statement>> disallowedStatements) {
        if (allowedStatements != null) {
            throw new IllegalArgumentException("You are not allowed to set both an allowed list and a disallowed list");
        }
        this.disallowedStatements = disallowedStatements;
    }

    /**
     * Legacy alias for {@link #setDisallowedStatements(List)}
     */
    @Deprecated
    public void setStatementsBlacklist(final List<Class<? extends Statement>> disallowedStatements) {
        setDisallowedStatements(disallowedStatements);
    }

    public List<Class<? extends Statement>> getAllowedStatements() {
        return allowedStatements;
    }

    /**
     * Legacy alias for {@link #getAllowedStatements()}
     */
    @Deprecated
    public List<Class<? extends Statement>> getStatementsWhitelist() {
        return getAllowedStatements();
    }

    public void setAllowedStatements(final List<Class<? extends Statement>> allowedStatements) {
        if (disallowedStatements != null) {
            throw new IllegalArgumentException("You are not allowed to set both an allowed list and a disallowed list");
        }
        this.allowedStatements = allowedStatements;
    }

    /**
     * Legacy alias for {@link #setAllowedStatements(List)}
     */
    @Deprecated
    public void setStatementsWhitelist(final List<Class<? extends Statement>> allowedStatements) {
        setAllowedStatements(allowedStatements);
    }

    public boolean isIndirectImportCheckEnabled() {
        return isIndirectImportCheckEnabled;
    }

    /**
     * Set this option to true if you want your import rules to be checked against every class node. This means that if
     * someone uses a fully qualified class name, then it will also be checked against the import rules, preventing, for
     * example, instantiation of classes without imports thanks to FQCN.
     *
     * @param indirectImportCheckEnabled set to true to enable indirect checks
     */
    public void setIndirectImportCheckEnabled(final boolean indirectImportCheckEnabled) {
        isIndirectImportCheckEnabled = indirectImportCheckEnabled;
    }

    public List<Integer> getDisallowedTokens() {
        return disallowedTokens;
    }

    /**
     * Legacy alias for {@link #getDisallowedTokens()}
     */
    @Deprecated
    public List<Integer> getTokensBlacklist() {
        return getDisallowedTokens();
    }

    /**
     * Sets the list of tokens which are not permitted.
     *
     * @param disallowedTokens the tokens. The values of the tokens must be those of {@link org.codehaus.groovy.syntax.Types}
     */
    public void setDisallowedTokens(final List<Integer> disallowedTokens) {
        if (allowedTokens != null) {
            throw new IllegalArgumentException("You are not allowed to set both an allowed list and a disallowed list");
        }
        this.disallowedTokens = disallowedTokens;
    }

    /**
     * Legacy alias for {@link #setDisallowedTokens(List)}.
     */
    @Deprecated
    public void setTokensBlacklist(final List<Integer> disallowedTokens) {
        setDisallowedTokens(disallowedTokens);
    }

    public List<Integer> getAllowedTokens() {
        return allowedTokens;
    }

    /**
     * Legacy alias for {@link #getAllowedTokens()}
     */
    @Deprecated
    public List<Integer> getTokensWhitelist() {
        return getAllowedTokens();
    }

    /**
     * Sets the list of tokens which are permitted.
     *
     * @param allowedTokens the tokens. The values of the tokens must be those of {@link org.codehaus.groovy.syntax.Types}
     */
    public void setAllowedTokens(final List<Integer> allowedTokens) {
        if (disallowedTokens != null) {
            throw new IllegalArgumentException("You are not allowed to set both an allowed list and a disallowed list");
        }
        this.allowedTokens = allowedTokens;
    }

    /**
     * Legacy alias for {@link #setAllowedTokens(List)}
     */
    @Deprecated
    public void setTokensWhitelist(final List<Integer> allowedTokens) {
        setAllowedTokens(allowedTokens);
    }

    public void addStatementCheckers(StatementChecker... checkers) {
        statementCheckers.addAll(Arrays.asList(checkers));
    }

    public void addExpressionCheckers(ExpressionChecker... checkers) {
        expressionCheckers.addAll(Arrays.asList(checkers));
    }

    public List<String> getDisallowedConstantTypes() {
        return disallowedConstantTypes;
    }

    /**
     * Legacy alias for {@link #getDisallowedConstantTypes()}
     */
    @Deprecated
    public List<String> getConstantTypesBlackList() {
        return getDisallowedConstantTypes();
    }

    public void setConstantTypesBlackList(final List<String> constantTypesBlackList) {
        if (allowedConstantTypes != null) {
            throw new IllegalArgumentException("You are not allowed to set both an allowed list and a disallowed list");
        }
        this.disallowedConstantTypes = constantTypesBlackList;
    }

    public List<String> getAllowedConstantTypes() {
        return allowedConstantTypes;
    }

    /**
     * Legacy alias for {@link #getAllowedStatements()}
     */
    @Deprecated
    public List<String> getConstantTypesWhiteList() {
        return getAllowedConstantTypes();
    }

    public void setAllowedConstantTypes(final List<String> allowedConstantTypes) {
        if (disallowedConstantTypes != null) {
            throw new IllegalArgumentException("You are not allowed to set both an allowed list and a disallowed list");
        }
        this.allowedConstantTypes = allowedConstantTypes;
    }

    /**
     * Legacy alias for {@link #setAllowedConstantTypes(List)}
     */
    @Deprecated
    public void setConstantTypesWhiteList(final List<String> allowedConstantTypes) {
        setAllowedConstantTypes(allowedConstantTypes);
    }

    /**
     * An alternative way of setting constant types.
     *
     * @param allowedConstantTypes a list of classes.
     */
    public void setAllowedConstantTypesClasses(final List<Class> allowedConstantTypes) {
        List<String> values = new LinkedList<>();
        for (Class aClass : allowedConstantTypes) {
            values.add(aClass.getName());
        }
        setConstantTypesWhiteList(values);
    }

    /**
     * Legacy alias for {@link #setAllowedConstantTypesClasses(List)}
     */
    @Deprecated
    public void setConstantTypesClassesWhiteList(final List<Class> allowedConstantTypes) {
        setAllowedConstantTypesClasses(allowedConstantTypes);
    }

    /**
     * An alternative way of setting constant types.
     *
     * @param disallowedConstantTypes a list of classes.
     */
    public void setDisallowedConstantTypesClasses(final List<Class> disallowedConstantTypes) {
        List<String> values = new LinkedList<>();
        for (Class aClass : disallowedConstantTypes) {
            values.add(aClass.getName());
        }
        setConstantTypesBlackList(values);
    }

    /**
     * Legacy alias for {@link #setDisallowedConstantTypesClasses(List)}
     */
    @Deprecated
    public void setConstantTypesClassesBlackList(final List<Class> disallowedConstantTypes) {
        setDisallowedConstantTypesClasses(disallowedConstantTypes);
    }

    public List<String> getDisallowedReceivers() {
        return disallowedReceivers;
    }

    /**
     * Legacy alias for {@link #getDisallowedReceivers()}
     */
    @Deprecated
    public List<String> getReceiversBlackList() {
        return getDisallowedReceivers();
    }

    /**
     * Sets the list of classes which deny method calls.
     *
     * Please note that since Groovy is a dynamic language, and
     * this class performs a static type check, it will be relatively
     * simple to bypass any disallowed list unless the disallowed receivers list contains, at
     * a minimum, Object, Script, GroovyShell, and Eval. Additionally,
     * it is necessary to also have MethodPointerExpression in the
     * disallowed expressions list for the disallowed receivers list to function
     * as a security check.
     *
     * @param disallowedReceivers the list of refused classes, as fully qualified names
     */
    public void setDisallowedReceivers(final List<String> disallowedReceivers) {
        if (allowedReceivers != null) {
            throw new IllegalArgumentException("You are not allowed to set both an allowed list and a disallowed list");
        }
        this.disallowedReceivers = disallowedReceivers;
    }

    /**
     * Legacy alias for {@link #setDisallowedReceivers(List)}
     */
    @Deprecated
    public void setReceiversBlackList(final List<String> disallowedReceivers) {
        setDisallowedReceivers(disallowedReceivers);
    }

    /**
     * An alternative way of setting {@link #setDisallowedReceivers(java.util.List) receiver classes}.
     *
     * @param disallowedReceivers a list of classes.
     */
    public void setDisallowedReceiversClasses(final List<Class> disallowedReceivers) {
        List<String> values = new LinkedList<>();
        for (Class aClass : disallowedReceivers) {
            values.add(aClass.getName());
        }
        setReceiversBlackList(values);
    }

    /**
     * Legacy alias for {@link #setDisallowedReceiversClasses(List)}.
     */
    @Deprecated
    public void setReceiversClassesBlackList(final List<Class> disallowedReceivers) {
        setDisallowedReceiversClasses(disallowedReceivers);
    }

    public List<String> getAllowedReceivers() {
        return allowedReceivers;
    }

    /**
     * Legacy alias for {@link #getAllowedReceivers()}
     */
    @Deprecated
    public List<String> getReceiversWhiteList() {
        return getAllowedReceivers();
    }

    /**
     * Sets the list of classes which may accept method calls.
     *
     * @param allowedReceivers the list of accepted classes, as fully qualified names
     */
    public void setAllowedReceivers(final List<String> allowedReceivers) {
        if (disallowedReceivers != null) {
            throw new IllegalArgumentException("You are not allowed to set both an allowed list and a disallowed list");
        }
        this.allowedReceivers = allowedReceivers;
    }

    /**
     * Legacy alias for {@link #setAllowedReceivers(List)}
     */
    @Deprecated
    public void setReceiversWhiteList(final List<String> allowedReceivers) {
        if (disallowedReceivers != null) {
            throw new IllegalArgumentException("You are not allowed to set both an allowed list and a disallowed list");
        }
        this.allowedReceivers = allowedReceivers;
    }

    /**
     * An alternative way of setting {@link #setReceiversWhiteList(java.util.List) receiver classes}.
     *
     * @param allowedReceivers a list of classes.
     */
    public void setAllowedReceiversClasses(final List<Class> allowedReceivers) {
        List<String> values = new LinkedList<>();
        for (Class aClass : allowedReceivers) {
            values.add(aClass.getName());
        }
        setReceiversWhiteList(values);
    }

    /**
     * Legacy alias for {@link #setAllowedReceiversClasses(List)}
     */
    @Deprecated
    public void setReceiversClassesWhiteList(final List<Class> allowedReceivers) {
        setAllowedReceiversClasses(allowedReceivers);
    }

    @Override
    public void call(final SourceUnit source, final GeneratorContext context, final ClassNode classNode) throws CompilationFailedException {
        ModuleNode ast = source.getAST();
        if (!isPackageAllowed && ast.getPackage() != null) {
            throw new SecurityException("Package definitions are not allowed");
        }
        checkMethodDefinitionAllowed(classNode);

        // verify imports
        if (disallowedImports != null || allowedImports != null || disallowedStarImports != null || allowedStarImports != null) {
            for (ImportNode importNode : ast.getImports()) {
                assertImportIsAllowed(importNode.getClassName());
            }
            for (ImportNode importNode : ast.getStarImports()) {
                assertStarImportIsAllowed(importNode.getPackageName() + "*");
            }
        }

        // verify static imports
        if (disallowedStaticImports != null || allowedStaticImports != null || disallowedStaticStarImports != null || allowedStaticStarImports != null) {
            for (Map.Entry<String, ImportNode> entry : ast.getStaticImports().entrySet()) {
                final String className = entry.getValue().getClassName();
                assertStaticImportIsAllowed(entry.getKey(), className);
            }
            for (Map.Entry<String, ImportNode> entry : ast.getStaticStarImports().entrySet()) {
                final String className = entry.getValue().getClassName();
                assertStaticImportIsAllowed(entry.getKey(), className);
            }
        }

        GroovyCodeVisitor visitor = createGroovyCodeVisitor();
        ast.getStatementBlock().visit(visitor);
        for (ClassNode clNode : ast.getClasses()) {
            if (clNode!=classNode) {
                checkMethodDefinitionAllowed(clNode);
                for (MethodNode methodNode : clNode.getMethods()) {
                    if (!methodNode.isSynthetic() && methodNode.getCode() != null) {
                        methodNode.getCode().visit(visitor);
                    }
                }
            }
        }

        List<MethodNode> methods = filterMethods(classNode);
        if (isMethodDefinitionAllowed) {
            for (MethodNode method : methods) {
                if (method.getDeclaringClass() == classNode && method.getCode() != null) {
                    method.getCode().visit(visitor);
                }
            }
        }
    }

    protected GroovyCodeVisitor createGroovyCodeVisitor() {
        return new SecuringCodeVisitor();
    }

    protected void checkMethodDefinitionAllowed(ClassNode owner) {
        if (isMethodDefinitionAllowed) return;
        List<MethodNode> methods = filterMethods(owner);
        if (!methods.isEmpty()) throw new SecurityException("Method definitions are not allowed");
    }

    protected static List<MethodNode> filterMethods(ClassNode owner) {
        List<MethodNode> result = new LinkedList<>();
        List<MethodNode> methods = owner.getMethods();
        for (MethodNode method : methods) {
            if (method.getDeclaringClass() == owner && !method.isSynthetic()) {
                if (("main".equals(method.getName()) || "run".equals(method.getName())) && method.isScriptBody()) continue;
                result.add(method);
            }
        }
        return result;
    }

    protected void assertStarImportIsAllowed(final String packageName) {
        if (allowedStarImports != null && !(allowedStarImports.contains(packageName)
                || allowedStarImports.stream().filter(it -> it.endsWith(".")).anyMatch(packageName::startsWith))) {
            throw new SecurityException("Importing [" + packageName + "] is not allowed");
        }
        if (disallowedStarImports != null && (disallowedStarImports.contains(packageName)
                || disallowedStarImports.stream().filter(it -> it.endsWith(".")).anyMatch(packageName::startsWith))) {
            throw new SecurityException("Importing [" + packageName + "] is not allowed");
        }
    }

    protected void assertImportIsAllowed(final String className) {
        if (allowedImports != null || allowedStarImports != null) {
            if (allowedImports != null && allowedImports.contains(className)) {
                return;
            }
            if (allowedStarImports != null) {
                String packageName = getWildCardImport(className);
                if (allowedStarImports.contains(packageName)
                        || allowedStarImports.stream().filter(it -> it.endsWith(".")).anyMatch(packageName::startsWith)) {
                    return;
                }
            }
            throw new SecurityException("Importing [" + className + "] is not allowed");
        } else {
            if (disallowedImports != null && disallowedImports.contains(className)) {
                throw new SecurityException("Importing [" + className + "] is not allowed");
            }
            if (disallowedStarImports != null) {
                String packageName = getWildCardImport(className);
                if (disallowedStarImports.contains(packageName) ||
                        disallowedStarImports.stream().filter(it -> it.endsWith(".")).anyMatch(packageName::startsWith)) {
                    throw new SecurityException("Importing [" + className + "] is not allowed");
                }
            }
        }
    }

    private String getWildCardImport(String className) {
        return className.substring(0, className.lastIndexOf('.') + 1) + "*";
    }

    protected void assertStaticImportIsAllowed(final String member, final String className) {
        final String fqn = className.equals(member) ? className : className + "." + member;
        if (allowedStaticImports != null && !allowedStaticImports.contains(fqn)) {
            if (allowedStaticStarImports != null) {
                // we should now check if the import is in the star imports
                String packageName = getWildCardImport(className);
                if (!allowedStaticStarImports.contains(className + ".*")
                        && allowedStaticStarImports.stream().filter(it -> it.endsWith(".")).noneMatch(packageName::startsWith)) {
                    throw new SecurityException("Importing [" + fqn + "] is not allowed");
                }
            } else {
                throw new SecurityException("Importing [" + fqn + "] is not allowed");
            }
        }
        if (disallowedStaticImports != null && disallowedStaticImports.contains(fqn)) {
            throw new SecurityException("Importing [" + fqn + "] is not allowed");
        }
        // check that there's no star import blacklist
        if (disallowedStaticStarImports != null) {
            String packageName = getWildCardImport(className);
            if (disallowedStaticStarImports.contains(className + ".*")
                    || disallowedStaticStarImports.stream().filter(it -> it.endsWith(".")).anyMatch(packageName::startsWith)) {
                throw new SecurityException("Importing [" + fqn + "] is not allowed");
            }
        }
    }

    /**
     * This visitor directly implements the {@link GroovyCodeVisitor} interface instead of using the {@link
     * CodeVisitorSupport} class to make sure that future features of the language gets managed by this visitor. Thus,
     * adding a new feature would result in a compilation error if this visitor is not updated.
     */
    protected class SecuringCodeVisitor implements GroovyCodeVisitor {

        /**
         * Checks that a given statement is either in the allowed list or not in the disallowed list.
         *
         * @param statement the statement to be checked
         * @throws SecurityException if usage of this statement class is forbidden
         */
        protected void assertStatementAuthorized(final Statement statement) throws SecurityException {
            final Class<? extends Statement> clazz = statement.getClass();
            if (disallowedStatements != null && disallowedStatements.contains(clazz)) {
                throw new SecurityException(clazz.getSimpleName() + "s are not allowed");
            } else if (allowedStatements != null && !allowedStatements.contains(clazz)) {
                throw new SecurityException(clazz.getSimpleName() + "s are not allowed");
            }
            for (StatementChecker statementChecker : statementCheckers) {
                if (!statementChecker.isAuthorized(statement)) {
                    throw new SecurityException("Statement [" + clazz.getSimpleName() + "] is not allowed");
                }
            }
        }

        /**
         * Checks that a given expression is either in the allowed list or not in the disallowed list.
         *
         * @param expression the expression to be checked
         * @throws SecurityException if usage of this expression class is forbidden
         */
        protected void assertExpressionAuthorized(final Expression expression) throws SecurityException {
            final Class<? extends Expression> clazz = expression.getClass();
            if (disallowedExpressions != null && disallowedExpressions.contains(clazz)) {
                throw new SecurityException(clazz.getSimpleName() + "s are not allowed: " + expression.getText());
            } else if (allowedExpressions != null && !allowedExpressions.contains(clazz)) {
                throw new SecurityException(clazz.getSimpleName() + "s are not allowed: " + expression.getText());
            }
            for (ExpressionChecker expressionChecker : expressionCheckers) {
                if (!expressionChecker.isAuthorized(expression)) {
                    throw new SecurityException("Expression [" + clazz.getSimpleName() + "] is not allowed: " + expression.getText());
                }
            }
            if (isIndirectImportCheckEnabled) {
                try {
                    if (expression instanceof ConstructorCallExpression) {
                        assertImportIsAllowed(expression.getType().getName());
                    } else if (expression instanceof MethodCallExpression) {
                        MethodCallExpression expr = (MethodCallExpression) expression;
                        ClassNode objectExpressionType = expr.getObjectExpression().getType();
                        final String typename = getExpressionType(objectExpressionType).getName();
                        assertImportIsAllowed(typename);
                        assertStaticImportIsAllowed(expr.getMethodAsString(), typename);
                    } else if (expression instanceof StaticMethodCallExpression) {
                        StaticMethodCallExpression expr = (StaticMethodCallExpression) expression;
                        final String typename = expr.getOwnerType().getName();
                        assertImportIsAllowed(typename);
                        assertStaticImportIsAllowed(expr.getMethod(), typename);
                    } else if (expression instanceof MethodPointerExpression) {
                        MethodPointerExpression expr = (MethodPointerExpression) expression;
                        final String typename = expr.getType().getName();
                        assertImportIsAllowed(typename);
                        assertStaticImportIsAllowed(expr.getText(), typename);
                    }
                } catch (SecurityException e) {
                    throw new SecurityException("Indirect import checks prevents usage of expression", e);
                }
            }
        }

        protected ClassNode getExpressionType(ClassNode objectExpressionType) {
            return objectExpressionType.isArray() ? getExpressionType(objectExpressionType.getComponentType()) : objectExpressionType;
        }

        /**
         * Checks that a given token is either in the allowed list or not in the disallowed list.
         *
         * @param token the token to be checked
         * @throws SecurityException if usage of this token is forbidden
         */
        protected void assertTokenAuthorized(final Token token) throws SecurityException {
            final int value = token.getType();
            if (disallowedTokens != null && disallowedTokens.contains(value)) {
                throw new SecurityException("Token " + token + " is not allowed");
            } else if (allowedTokens != null && !allowedTokens.contains(value)) {
                throw new SecurityException("Token " + token + " is not allowed");
            }
        }

        @Override
        public void visitBlockStatement(final BlockStatement block) {
            assertStatementAuthorized(block);
            for (Statement statement : block.getStatements()) {
                statement.visit(this);
            }
        }

        @Override
        public void visitForLoop(final ForStatement forLoop) {
            assertStatementAuthorized(forLoop);
            forLoop.getCollectionExpression().visit(this);
            forLoop.getLoopBlock().visit(this);
        }

        @Override
        public void visitWhileLoop(final WhileStatement loop) {
            assertStatementAuthorized(loop);
            loop.getBooleanExpression().visit(this);
            loop.getLoopBlock().visit(this);
        }

        @Override
        public void visitDoWhileLoop(final DoWhileStatement loop) {
            assertStatementAuthorized(loop);
            loop.getBooleanExpression().visit(this);
            loop.getLoopBlock().visit(this);
        }

        @Override
        public void visitIfElse(final IfStatement ifElse) {
            assertStatementAuthorized(ifElse);
            ifElse.getBooleanExpression().visit(this);
            ifElse.getIfBlock().visit(this);
            ifElse.getElseBlock().visit(this);
        }

        @Override
        public void visitExpressionStatement(final ExpressionStatement statement) {
            assertStatementAuthorized(statement);
            statement.getExpression().visit(this);
        }

        @Override
        public void visitReturnStatement(final ReturnStatement statement) {
            assertStatementAuthorized(statement);
            statement.getExpression().visit(this);
        }

        @Override
        public void visitAssertStatement(final AssertStatement statement) {
            assertStatementAuthorized(statement);
            statement.getBooleanExpression().visit(this);
            statement.getMessageExpression().visit(this);
        }

        @Override
        public void visitTryCatchFinally(final TryCatchStatement statement) {
            assertStatementAuthorized(statement);
            statement.getTryStatement().visit(this);
            for (CatchStatement catchStatement : statement.getCatchStatements()) {
                catchStatement.visit(this);
            }
            statement.getFinallyStatement().visit(this);
        }

        @Override
        public void visitEmptyStatement(EmptyStatement statement) {
            // noop
        }

        @Override
        public void visitSwitch(final SwitchStatement statement) {
            assertStatementAuthorized(statement);
            statement.getExpression().visit(this);
            for (CaseStatement caseStatement : statement.getCaseStatements()) {
                caseStatement.visit(this);
            }
            statement.getDefaultStatement().visit(this);
        }

        @Override
        public void visitCaseStatement(final CaseStatement statement) {
            assertStatementAuthorized(statement);
            statement.getExpression().visit(this);
            statement.getCode().visit(this);
        }

        @Override
        public void visitBreakStatement(final BreakStatement statement) {
            assertStatementAuthorized(statement);
        }

        @Override
        public void visitContinueStatement(final ContinueStatement statement) {
            assertStatementAuthorized(statement);
        }

        @Override
        public void visitThrowStatement(final ThrowStatement statement) {
            assertStatementAuthorized(statement);
            statement.getExpression().visit(this);
        }

        @Override
        public void visitSynchronizedStatement(final SynchronizedStatement statement) {
            assertStatementAuthorized(statement);
            statement.getExpression().visit(this);
            statement.getCode().visit(this);
        }

        @Override
        public void visitCatchStatement(final CatchStatement statement) {
            assertStatementAuthorized(statement);
            statement.getCode().visit(this);
        }

        @Override
        public void visitMethodCallExpression(final MethodCallExpression call) {
            assertExpressionAuthorized(call);
            Expression receiver = call.getObjectExpression();
            final String typeName = receiver.getType().getName();
            if (allowedReceivers != null && !allowedReceivers.contains(typeName)) {
                throw new SecurityException("Method calls not allowed on [" + typeName + "]");
            } else if (disallowedReceivers != null && disallowedReceivers.contains(typeName)) {
                throw new SecurityException("Method calls not allowed on [" + typeName + "]");
            }
            receiver.visit(this);
            final Expression method = call.getMethod();
            checkConstantTypeIfNotMethodNameOrProperty(method);
            call.getArguments().visit(this);
        }

        @Override
        public void visitStaticMethodCallExpression(final StaticMethodCallExpression call) {
            assertExpressionAuthorized(call);
            final String typeName = call.getOwnerType().getName();
            if (allowedReceivers != null && !allowedReceivers.contains(typeName)) {
                throw new SecurityException("Method calls not allowed on [" + typeName + "]");
            } else if (disallowedReceivers != null && disallowedReceivers.contains(typeName)) {
                throw new SecurityException("Method calls not allowed on [" + typeName + "]");
            }
            call.getArguments().visit(this);
        }

        @Override
        public void visitConstructorCallExpression(final ConstructorCallExpression call) {
            assertExpressionAuthorized(call);
            call.getArguments().visit(this);
        }

        @Override
        public void visitTernaryExpression(final TernaryExpression expression) {
            assertExpressionAuthorized(expression);
            expression.getBooleanExpression().visit(this);
            expression.getTrueExpression().visit(this);
            expression.getFalseExpression().visit(this);
        }

        @Override
        public void visitShortTernaryExpression(final ElvisOperatorExpression expression) {
            assertExpressionAuthorized(expression);
            visitTernaryExpression(expression);
        }

        @Override
        public void visitBinaryExpression(final BinaryExpression expression) {
            assertExpressionAuthorized(expression);
            assertTokenAuthorized(expression.getOperation());
            expression.getLeftExpression().visit(this);
            expression.getRightExpression().visit(this);
        }

        @Override
        public void visitPrefixExpression(final PrefixExpression expression) {
            assertExpressionAuthorized(expression);
            assertTokenAuthorized(expression.getOperation());
            expression.getExpression().visit(this);
        }

        @Override
        public void visitPostfixExpression(final PostfixExpression expression) {
            assertExpressionAuthorized(expression);
            assertTokenAuthorized(expression.getOperation());
            expression.getExpression().visit(this);
        }

        @Override
        public void visitBooleanExpression(final BooleanExpression expression) {
            assertExpressionAuthorized(expression);
            expression.getExpression().visit(this);
        }

        @Override
        public void visitClosureExpression(final ClosureExpression expression) {
            assertExpressionAuthorized(expression);
            if (!isClosuresAllowed) throw new SecurityException("Closures are not allowed");
            expression.getCode().visit(this);
        }

        @Override
        public void visitLambdaExpression(LambdaExpression expression) {
            visitClosureExpression(expression);
        }

        @Override
        public void visitTupleExpression(final TupleExpression expression) {
            assertExpressionAuthorized(expression);
            visitListOfExpressions(expression.getExpressions());
        }

        @Override
        public void visitMapExpression(final MapExpression expression) {
            assertExpressionAuthorized(expression);
            visitListOfExpressions(expression.getMapEntryExpressions());
        }

        @Override
        public void visitMapEntryExpression(final MapEntryExpression expression) {
            assertExpressionAuthorized(expression);
            expression.getKeyExpression().visit(this);
            expression.getValueExpression().visit(this);
        }

        @Override
        public void visitListExpression(final ListExpression expression) {
            assertExpressionAuthorized(expression);
            visitListOfExpressions(expression.getExpressions());
        }

        @Override
        public void visitRangeExpression(final RangeExpression expression) {
            assertExpressionAuthorized(expression);
            expression.getFrom().visit(this);
            expression.getTo().visit(this);
        }

        @Override
        public void visitPropertyExpression(final PropertyExpression expression) {
            assertExpressionAuthorized(expression);
            Expression receiver = expression.getObjectExpression();
            final String typeName = receiver.getType().getName();
            if (allowedReceivers != null && !allowedReceivers.contains(typeName)) {
                throw new SecurityException("Property access not allowed on [" + typeName + "]");
            } else if (disallowedReceivers != null && disallowedReceivers.contains(typeName)) {
                throw new SecurityException("Property access not allowed on [" + typeName + "]");
            }
            receiver.visit(this);
            final Expression property = expression.getProperty();
            checkConstantTypeIfNotMethodNameOrProperty(property);
        }

        private void checkConstantTypeIfNotMethodNameOrProperty(final Expression expr) {
            if (expr instanceof ConstantExpression) {
                if (!"java.lang.String".equals(expr.getType().getName())) {
                    expr.visit(this);
                }
            } else {
                expr.visit(this);
            }
        }

        @Override
        public void visitAttributeExpression(final AttributeExpression expression) {
            assertExpressionAuthorized(expression);
            Expression receiver = expression.getObjectExpression();
            final String typeName = receiver.getType().getName();
            if (allowedReceivers != null && !allowedReceivers.contains(typeName)) {
                throw new SecurityException("Attribute access not allowed on [" + typeName + "]");
            } else if (disallowedReceivers != null && disallowedReceivers.contains(typeName)) {
                throw new SecurityException("Attribute access not allowed on [" + typeName + "]");
            }
            receiver.visit(this);
            final Expression property = expression.getProperty();
            checkConstantTypeIfNotMethodNameOrProperty(property);
        }

        @Override
        public void visitFieldExpression(final FieldExpression expression) {
            assertExpressionAuthorized(expression);
        }

        @Override
        public void visitMethodPointerExpression(final MethodPointerExpression expression) {
            assertExpressionAuthorized(expression);
            expression.getExpression().visit(this);
            expression.getMethodName().visit(this);
        }

        @Override
        public void visitMethodReferenceExpression(final MethodReferenceExpression expression) {
            visitMethodPointerExpression(expression);
        }

        @Override
        public void visitConstantExpression(final ConstantExpression expression) {
            assertExpressionAuthorized(expression);
            final String type = expression.getType().getName();
            if (allowedConstantTypes != null && !allowedConstantTypes.contains(type)) {
                throw new SecurityException("Constant expression type [" + type + "] is not allowed");
            }
            if (disallowedConstantTypes != null && disallowedConstantTypes.contains(type)) {
                throw new SecurityException("Constant expression type [" + type + "] is not allowed");
            }
        }

        @Override
        public void visitClassExpression(final ClassExpression expression) {
            assertExpressionAuthorized(expression);
        }

        @Override
        public void visitVariableExpression(final VariableExpression expression) {
            assertExpressionAuthorized(expression);
            final String type = expression.getType().getName();
            if (allowedConstantTypes != null && !allowedConstantTypes.contains(type)) {
                throw new SecurityException("Usage of variables of type [" + type + "] is not allowed");
            }
            if (disallowedConstantTypes != null && disallowedConstantTypes.contains(type)) {
                throw new SecurityException("Usage of variables of type [" + type + "] is not allowed");
            }
        }

        @Override
        public void visitDeclarationExpression(final DeclarationExpression expression) {
            assertExpressionAuthorized(expression);
            visitBinaryExpression(expression);
        }

        @Override
        public void visitGStringExpression(final GStringExpression expression) {
            assertExpressionAuthorized(expression);
            visitListOfExpressions(expression.getStrings());
            visitListOfExpressions(expression.getValues());
        }

        @Override
        public void visitArrayExpression(final ArrayExpression expression) {
            assertExpressionAuthorized(expression);
            visitListOfExpressions(expression.getExpressions());
            visitListOfExpressions(expression.getSizeExpression());
        }

        @Override
        public void visitSpreadExpression(final SpreadExpression expression) {
            assertExpressionAuthorized(expression);
            expression.getExpression().visit(this);
        }

        @Override
        public void visitSpreadMapExpression(final SpreadMapExpression expression) {
            assertExpressionAuthorized(expression);
            expression.getExpression().visit(this);
        }

        @Override
        public void visitNotExpression(final NotExpression expression) {
            assertExpressionAuthorized(expression);
            expression.getExpression().visit(this);
        }

        @Override
        public void visitUnaryMinusExpression(final UnaryMinusExpression expression) {
            assertExpressionAuthorized(expression);
            expression.getExpression().visit(this);
        }

        @Override
        public void visitUnaryPlusExpression(final UnaryPlusExpression expression) {
            assertExpressionAuthorized(expression);
            expression.getExpression().visit(this);
        }

        @Override
        public void visitBitwiseNegationExpression(final BitwiseNegationExpression expression) {
            assertExpressionAuthorized(expression);
            expression.getExpression().visit(this);
        }

        @Override
        public void visitCastExpression(final CastExpression expression) {
            assertExpressionAuthorized(expression);
            expression.getExpression().visit(this);
        }

        @Override
        public void visitArgumentlistExpression(final ArgumentListExpression expression) {
            assertExpressionAuthorized(expression);
            visitTupleExpression(expression);
        }

        @Override
        public void visitClosureListExpression(final ClosureListExpression closureListExpression) {
            assertExpressionAuthorized(closureListExpression);
            if (!isClosuresAllowed) throw new SecurityException("Closures are not allowed");
            visitListOfExpressions(closureListExpression.getExpressions());
        }

        @Override
        public void visitBytecodeExpression(final BytecodeExpression expression) {
            assertExpressionAuthorized(expression);
        }
    }

    /**
     * This interface allows the user to provide a custom expression checker if the dis/allowed expression lists are not
     * sufficient
     */
    @FunctionalInterface
    public interface ExpressionChecker {
        boolean isAuthorized(Expression expression);
    }

    /**
     * This interface allows the user to provide a custom statement checker if the dis/allowed statement lists are not
     * sufficient
     */
    @FunctionalInterface
    public interface StatementChecker {
        boolean isAuthorized(Statement expression);
    }
}
