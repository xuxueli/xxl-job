/*!
 * jquery.fancytree.filter.js
 *
 * Remove or highlight tree nodes, based on a filter.
 * (Extension module for jquery.fancytree.js: https://github.com/mar10/fancytree/)
 *
 * Copyright (c) 2008-2020, Martin Wendt (https://wwWendt.de)
 *
 * Released under the MIT license
 * https://github.com/mar10/fancytree/wiki/LicenseInfo
 *
 * @version @VERSION
 * @date @DATE
 */

(function(factory) {
	if (typeof define === "function" && define.amd) {
		// AMD. Register as an anonymous module.
		define(["jquery", "./jquery.fancytree"], factory);
	} else if (typeof module === "object" && module.exports) {
		// Node/CommonJS
		require("./jquery.fancytree");
		module.exports = factory(require("jquery"));
	} else {
		// Browser globals
		factory(jQuery);
	}
})(function($) {
	"use strict";

	/*******************************************************************************
	 * Private functions and variables
	 */

	var KeyNoData = "__not_found__",
		escapeHtml = $.ui.fancytree.escapeHtml;

	function _escapeRegex(str) {
		return (str + "").replace(/([.?*+^$[\]\\(){}|-])/g, "\\$1");
	}

	function extractHtmlText(s) {
		if (s.indexOf(">") >= 0) {
			return $("<div/>")
				.html(s)
				.text();
		}
		return s;
	}

	$.ui.fancytree._FancytreeClass.prototype._applyFilterImpl = function(
		filter,
		branchMode,
		_opts
	) {
		var match,
			statusNode,
			re,
			reHighlight,
			temp,
			prevEnableUpdate,
			count = 0,
			treeOpts = this.options,
			escapeTitles = treeOpts.escapeTitles,
			prevAutoCollapse = treeOpts.autoCollapse,
			opts = $.extend({}, treeOpts.filter, _opts),
			hideMode = opts.mode === "hide",
			leavesOnly = !!opts.leavesOnly && !branchMode;

		// Default to 'match title substring (not case sensitive)'
		if (typeof filter === "string") {
			if (filter === "") {
				this.warn(
					"Fancytree passing an empty string as a filter is handled as clearFilter()."
				);
				this.clearFilter();
				return;
			}
			if (opts.fuzzy) {
				// See https://codereview.stackexchange.com/questions/23899/faster-javascript-fuzzy-string-matching-function/23905#23905
				// and http://www.quora.com/How-is-the-fuzzy-search-algorithm-in-Sublime-Text-designed
				// and http://www.dustindiaz.com/autocomplete-fuzzy-matching
				match = filter.split("").reduce(function(a, b) {
					return a + "[^" + b + "]*" + b;
				});
			} else {
				match = _escapeRegex(filter); // make sure a '.' is treated literally
			}
			re = new RegExp(".*" + match + ".*", "i");
			reHighlight = new RegExp(_escapeRegex(filter), "gi");
			filter = function(node) {
				if (!node.title) {
					return false;
				}
				var text = escapeTitles
						? node.title
						: extractHtmlText(node.title),
					res = !!re.test(text);

				if (res && opts.highlight) {
					if (escapeTitles) {
						// #740: we must not apply the marks to escaped entity names, e.g. `&quot;`
						// Use some exotic characters to mark matches:
						temp = text.replace(reHighlight, function(s) {
							return "\uFFF7" + s + "\uFFF8";
						});
						// now we can escape the title...
						node.titleWithHighlight = escapeHtml(temp)
							// ... and finally insert the desired `<mark>` tags
							.replace(/\uFFF7/g, "<mark>")
							.replace(/\uFFF8/g, "</mark>");
					} else {
						node.titleWithHighlight = text.replace(
							reHighlight,
							function(s) {
								return "<mark>" + s + "</mark>";
							}
						);
					}
					// node.debug("filter", escapeTitles, text, node.titleWithHighlight);
				}
				return res;
			};
		}

		this.enableFilter = true;
		this.lastFilterArgs = arguments;

		prevEnableUpdate = this.enableUpdate(false);

		this.$div.addClass("fancytree-ext-filter");
		if (hideMode) {
			this.$div.addClass("fancytree-ext-filter-hide");
		} else {
			this.$div.addClass("fancytree-ext-filter-dimm");
		}
		this.$div.toggleClass(
			"fancytree-ext-filter-hide-expanders",
			!!opts.hideExpanders
		);
		// Reset current filter
		this.rootNode.subMatchCount = 0;
		this.visit(function(node) {
			delete node.match;
			delete node.titleWithHighlight;
			node.subMatchCount = 0;
		});
		statusNode = this.getRootNode()._findDirectChild(KeyNoData);
		if (statusNode) {
			statusNode.remove();
		}

		// Adjust node.hide, .match, and .subMatchCount properties
		treeOpts.autoCollapse = false; // #528

		this.visit(function(node) {
			if (leavesOnly && node.children != null) {
				return;
			}
			var res = filter(node),
				matchedByBranch = false;

			if (res === "skip") {
				node.visit(function(c) {
					c.match = false;
				}, true);
				return "skip";
			}
			if (!res && (branchMode || res === "branch") && node.parent.match) {
				res = true;
				matchedByBranch = true;
			}
			if (res) {
				count++;
				node.match = true;
				node.visitParents(function(p) {
					if (p !== node) {
						p.subMatchCount += 1;
					}
					// Expand match (unless this is no real match, but only a node in a matched branch)
					if (opts.autoExpand && !matchedByBranch && !p.expanded) {
						p.setExpanded(true, {
							noAnimation: true,
							noEvents: true,
							scrollIntoView: false,
						});
						p._filterAutoExpanded = true;
					}
				}, true);
			}
		});
		treeOpts.autoCollapse = prevAutoCollapse;

		if (count === 0 && opts.nodata && hideMode) {
			statusNode = opts.nodata;
			if ($.isFunction(statusNode)) {
				statusNode = statusNode();
			}
			if (statusNode === true) {
				statusNode = {};
			} else if (typeof statusNode === "string") {
				statusNode = { title: statusNode };
			}
			statusNode = $.extend(
				{
					statusNodeType: "nodata",
					key: KeyNoData,
					title: this.options.strings.noData,
				},
				statusNode
			);

			this.getRootNode().addNode(statusNode).match = true;
		}
		// Redraw whole tree
		this._callHook("treeStructureChanged", this, "applyFilter");
		// this.render();
		this.enableUpdate(prevEnableUpdate);
		return count;
	};

	/**
	 * [ext-filter] Dimm or hide nodes.
	 *
	 * @param {function | string} filter
	 * @param {boolean} [opts={autoExpand: false, leavesOnly: false}]
	 * @returns {integer} count
	 * @alias Fancytree#filterNodes
	 * @requires jquery.fancytree.filter.js
	 */
	$.ui.fancytree._FancytreeClass.prototype.filterNodes = function(
		filter,
		opts
	) {
		if (typeof opts === "boolean") {
			opts = { leavesOnly: opts };
			this.warn(
				"Fancytree.filterNodes() leavesOnly option is deprecated since 2.9.0 / 2015-04-19. Use opts.leavesOnly instead."
			);
		}
		return this._applyFilterImpl(filter, false, opts);
	};

	/**
	 * [ext-filter] Dimm or hide whole branches.
	 *
	 * @param {function | string} filter
	 * @param {boolean} [opts={autoExpand: false}]
	 * @returns {integer} count
	 * @alias Fancytree#filterBranches
	 * @requires jquery.fancytree.filter.js
	 */
	$.ui.fancytree._FancytreeClass.prototype.filterBranches = function(
		filter,
		opts
	) {
		return this._applyFilterImpl(filter, true, opts);
	};

	/**
	 * [ext-filter] Reset the filter.
	 *
	 * @alias Fancytree#clearFilter
	 * @requires jquery.fancytree.filter.js
	 */
	$.ui.fancytree._FancytreeClass.prototype.clearFilter = function() {
		var $title,
			statusNode = this.getRootNode()._findDirectChild(KeyNoData),
			escapeTitles = this.options.escapeTitles,
			enhanceTitle = this.options.enhanceTitle,
			prevEnableUpdate = this.enableUpdate(false);

		if (statusNode) {
			statusNode.remove();
		}
		// we also counted root node's subMatchCount
		delete this.rootNode.match;
		delete this.rootNode.subMatchCount;

		this.visit(function(node) {
			if (node.match && node.span) {
				// #491, #601
				$title = $(node.span).find(">span.fancytree-title");
				if (escapeTitles) {
					$title.text(node.title);
				} else {
					$title.html(node.title);
				}
				if (enhanceTitle) {
					enhanceTitle(
						{ type: "enhanceTitle" },
						{ node: node, $title: $title }
					);
				}
			}
			delete node.match;
			delete node.subMatchCount;
			delete node.titleWithHighlight;
			if (node.$subMatchBadge) {
				node.$subMatchBadge.remove();
				delete node.$subMatchBadge;
			}
			if (node._filterAutoExpanded && node.expanded) {
				node.setExpanded(false, {
					noAnimation: true,
					noEvents: true,
					scrollIntoView: false,
				});
			}
			delete node._filterAutoExpanded;
		});
		this.enableFilter = false;
		this.lastFilterArgs = null;
		this.$div.removeClass(
			"fancytree-ext-filter fancytree-ext-filter-dimm fancytree-ext-filter-hide"
		);
		this._callHook("treeStructureChanged", this, "clearFilter");
		// this.render();
		this.enableUpdate(prevEnableUpdate);
	};

	/**
	 * [ext-filter] Return true if a filter is currently applied.
	 *
	 * @returns {Boolean}
	 * @alias Fancytree#isFilterActive
	 * @requires jquery.fancytree.filter.js
	 * @since 2.13
	 */
	$.ui.fancytree._FancytreeClass.prototype.isFilterActive = function() {
		return !!this.enableFilter;
	};

	/**
	 * [ext-filter] Return true if this node is matched by current filter (or no filter is active).
	 *
	 * @returns {Boolean}
	 * @alias FancytreeNode#isMatched
	 * @requires jquery.fancytree.filter.js
	 * @since 2.13
	 */
	$.ui.fancytree._FancytreeNodeClass.prototype.isMatched = function() {
		return !(this.tree.enableFilter && !this.match);
	};

	/*******************************************************************************
	 * Extension code
	 */
	$.ui.fancytree.registerExtension({
		name: "filter",
		version: "@VERSION",
		// Default options for this extension.
		options: {
			autoApply: true, // Re-apply last filter if lazy data is loaded
			autoExpand: false, // Expand all branches that contain matches while filtered
			counter: true, // Show a badge with number of matching child nodes near parent icons
			fuzzy: false, // Match single characters in order, e.g. 'fb' will match 'FooBar'
			hideExpandedCounter: true, // Hide counter badge if parent is expanded
			hideExpanders: false, // Hide expanders if all child nodes are hidden by filter
			highlight: true, // Highlight matches by wrapping inside <mark> tags
			leavesOnly: false, // Match end nodes only
			nodata: true, // Display a 'no data' status node if result is empty
			mode: "dimm", // Grayout unmatched nodes (pass "hide" to remove unmatched node instead)
		},
		nodeLoadChildren: function(ctx, source) {
			var tree = ctx.tree;

			return this._superApply(arguments).done(function() {
				if (
					tree.enableFilter &&
					tree.lastFilterArgs &&
					ctx.options.filter.autoApply
				) {
					tree._applyFilterImpl.apply(tree, tree.lastFilterArgs);
				}
			});
		},
		nodeSetExpanded: function(ctx, flag, callOpts) {
			var node = ctx.node;

			delete node._filterAutoExpanded;
			// Make sure counter badge is displayed again, when node is beeing collapsed
			if (
				!flag &&
				ctx.options.filter.hideExpandedCounter &&
				node.$subMatchBadge
			) {
				node.$subMatchBadge.show();
			}
			return this._superApply(arguments);
		},
		nodeRenderStatus: function(ctx) {
			// Set classes for current status
			var res,
				node = ctx.node,
				tree = ctx.tree,
				opts = ctx.options.filter,
				$title = $(node.span).find("span.fancytree-title"),
				$span = $(node[tree.statusClassPropName]),
				enhanceTitle = ctx.options.enhanceTitle,
				escapeTitles = ctx.options.escapeTitles;

			res = this._super(ctx);
			// nothing to do, if node was not yet rendered
			if (!$span.length || !tree.enableFilter) {
				return res;
			}
			$span
				.toggleClass("fancytree-match", !!node.match)
				.toggleClass("fancytree-submatch", !!node.subMatchCount)
				.toggleClass(
					"fancytree-hide",
					!(node.match || node.subMatchCount)
				);
			// Add/update counter badge
			if (
				opts.counter &&
				node.subMatchCount &&
				(!node.isExpanded() || !opts.hideExpandedCounter)
			) {
				if (!node.$subMatchBadge) {
					node.$subMatchBadge = $(
						"<span class='fancytree-childcounter'/>"
					);
					$(
						"span.fancytree-icon, span.fancytree-custom-icon",
						node.span
					).append(node.$subMatchBadge);
				}
				node.$subMatchBadge.show().text(node.subMatchCount);
			} else if (node.$subMatchBadge) {
				node.$subMatchBadge.hide();
			}
			// node.debug("nodeRenderStatus", node.titleWithHighlight, node.title)
			// #601: also check for $title.length, because we don't need to render
			// if node.span is null (i.e. not rendered)
			if (node.span && (!node.isEditing || !node.isEditing.call(node))) {
				if (node.titleWithHighlight) {
					$title.html(node.titleWithHighlight);
				} else if (escapeTitles) {
					$title.text(node.title);
				} else {
					$title.html(node.title);
				}
				if (enhanceTitle) {
					enhanceTitle(
						{ type: "enhanceTitle" },
						{ node: node, $title: $title }
					);
				}
			}
			return res;
		},
	});
	// Value returned by `require('jquery.fancytree..')`
	return $.ui.fancytree;
}); // End of closure
