/******************************************************************************
 * Virtual objects for jsdoc documentation
 */

// Allow unused variables for demonstration
/* eslint-disable no-unused-vars, one-var */

/**
 * Context object passed to events and hook functions.
 * @name EventData
 * @type {Object}
 *
 * @property {Fancytree} tree The tree instance
 * @property {object} widget The <a href="http://api.jqueryui.com/jQuery.widget">jQuery UI tree widget</a>
 * @property {FancytreeOptions} options Shortcut to tree.options
 * @property {Event} originalEvent The <a href="http://api.jquery.com/category/events/event-object">jQuery Event</a> that initially triggered this call
 * @property {FancytreeNode | null} node The node that this call applies to (`null` for tree events)
 * @property {any} result (output parameter) Event handlers can return values back to the caller. Used by `lazyLoad`, `postProcess`, ...
 * @property {string | undefined} targetType (only for click and dblclick events) 'title' | 'prefix' | 'expander' | 'checkbox' | 'icon'
 * @property {object} typeInfo Contains `tree.types[node.type]` (or `{}` if node does not have a type). @since 2.27
 * @property {any} response (only for postProcess event) Original ajax response
 */
var EventData = {};

/**
 * Data object passed to FancytreeNode() constructor.
 * Note: typically these attributes are accessed by class methods, e.g. `node.isExpanded()`
 * and `node.setTitle("foo")`.
 *
 * @name NodeData
 * @type {Object}
 *
 * @property {boolean} active Evaluated during initialization: If true, `node.setActive()` is called after loading.
 *
 * @property {boolean|string} checkbox Pass `false` to remove checkbox for this node.<br>
 *     Note that selection via the API or initialization data is still possible, even
 *     if no checkbox is displayed.<br>
 *     undefined: Use global tree option of the same name<br>
 *     true: display a checkbox<br>
 *     false: hide checkbox<br>
 *     "radio": display a radio button (this does not have any effect on the selection behavior)<br>
 *     This is a <a href="https://github.com/mar10/fancytree/wiki#dynamic-options">Dynamic Option</a>:
 *     see also the global tree option of the same name.
 * @property {NodeData[]} children Optional array of child nodes.<br>
 *     Note that for <i>lazy</i> nodes, a value of null or undefined is interpreted as
 *     <i>not yet loaded</i>; if an array is passed (even an empty one), the
 *     node is considered <i>loaded</i> which will effectively disable the
 *     lazy-behavior.
 * @property {object} data All unknown properties from constructor will be copied to `node.data`.
 * @property {boolean} expanded Initial expansion state. Use `node.setExpanded()` or `node.isExpanded()` to access.
 * @property {string} extraClasses Class names added to the node markup (separate with space).<br>
 *     Note: use `node.add/remove/toggleClass()` to modify.
 * @property {boolean} focus (Initialization only, but will not be stored  with the node.)
 * @property {boolean} folder Folders have different default icons and honor the `clickFolderMode` option.
 * @property {boolean} <del>hideCheckbox</del>  @deprecated use `checkbox` instead.<br>
 *     (The 'hideCheckbox' class name is still recognized when html source input is parsed.)
 * @property {boolean|string} icon Define this node's icon.<br>
 *     undefined: Use global tree option of the same name<br>
 *     true: Use default icon, depending on `node.folder` and `node.expanded` status<br>
 *     false: Hide icon<br>
 *     Object: Dict of options, e.g. {text: 'check_box', addClass: ''}, useful for
 *     ligature fonts like Material.<br>
 *     String: A string value that contains a '/' or a '.' is used as `src` attribute for a `<img>` tag.
 *     (See also the global `imagePath` option.)<br>
 *     Any other string value is used to generate custom tags, e.g. for "ui-icon ui-icon-heart":<br>
 *     `<span class="fancytree-custom-icon ui-icon ui-icon-heart" />`.<br>
 *     See also <a href="https://github.com/mar10/fancytree/wiki#dynamic-options">dynamic options</a>.<br>
 * @property {string} <del>iconclass</del> @deprecated use `icon` instead.
 * @property {string} iconTooltip Will be added as `title` attribute of the node's icon span,
 *     thus enabling a tooltip.<br>
 *     NOTE: Passing a tooltip string per node is probably very inefficient, assuming the
 *     same data would be passed for every node of the same type: Consider using
 *     the <a href="https://github.com/mar10/fancytree/wiki/TutorialNodeTypes">tree.types pattern</a>.<br>
 *     See also the global `tree.iconTooltip` option.
 * @property {string} key Unique key for this node (auto-generated if omitted).
 * @property {boolean} lazy Lazy folders call the `lazyLoad` on first expand to load their children.
 * @property {string} refKey (Reserved, used by 'clones' extension.)
 * @property {boolean} selected Initial selection state. Use `node.setSelected()` or `node.isSelected()` to access.
 * @property {string} statusNodeType If set, make this node a status node. Values: 'error', 'loading', 'nodata', 'paging'.
 * @property {string} title Node text (may contain HTML tags). Use `node.setTitle()` to modify.
 * @property {string} tooltip Will be added as `title` attribute of the node's title span,
 *     thus enabling a tooltip.<br>
 *     NOTE: Passing a tooltip string per node would be inefficient, if it can be constructed
 *     from existing node data. Consider using the callback instead:<br>
 *     See also the global `tree.tooltip` option.
 * @property {string} type Made available as `node.type`.<br>
 *     Should match an `tree.types` entry.
 *     See also <a href="https://github.com/mar10/fancytree/wiki/TutorialNodeTypes">node types</a>.
 *     @since 2.27
 * @property {boolean} unselectable Prevent (de-)selection using mouse or keyboard.<br>
 *     Note: This node can still be (de)selected by status propagation in selectMode 3.
 *     (Set `unselectableStatus` to prevent this.)
 *     @since 2.23
 * @property {boolean} unselectableIgnore Ignore this node when calculating the `partsel`
 *     status of parent nodes in selectMode 3 propagation.<br>
 *     If defined, `unselectable: true` is implied.
 *     @since 2.23
 * @property {boolean} unselectableStatus Use this as constant `selected` value
 *     (overriding selectMode 3 propagation).<br>
 *     If defined, `unselectable: true` is implied.
 *     @since 2.23
 * @property {any} OTHER Attributes other than listed above will be copied to `node.data`.
 *
 */
var NodeData = {};

/**
 * Data object similar to {@link NodeData}, but with additional options.
 * May be passed to {@link FancytreeNode#applyPatch}
 * (Every property that is omitted (or set to undefined) will be ignored)
 * @name NodePatch
 * @type {Object}
 *
 * @property {any} any (see NodeData)
 * @property {NodeData} appendChildren (not yet implemented)
 * @property {NodeData} replaceChildren (not yet implemented)
 * @property {NodeData} insertChildren (not yet implemented)
 */
var NodePatch = {};

/**
 * List of [key, {@link NodePatch}]  tuples.
 * May be passed to {@link Fancytree#applyPatch}.
 *
 * @name TreePatch
 * @type {Object}
 *
 */
var TreePatch = {};

/**
 * @name FancytreeOptions
 * @type {Object}
 *
 * @description
 * Fancytree options (see also example)
 *
 * @example $("#tree").fancytree({ source: { url: "/myService" } });
 *
 * @property {boolean} activeVisible Make sure that the active node is always visible, i.e. its parents are expanded (default: true).
 * @property {object} ajax Default options for ajax requests
 * @property {boolean} aria (default: true) Add WAI-ARIA attributes to markup
 * @property {boolean} autoActivate Activate a node when focused with the keyboard (default: true)
 * @property {boolean} autoCollapse Automatically collapse all siblings, when a node is expanded (default: false).
 * @property {boolean} autoScroll Scroll node into visible area, when focused by keyboard (default: false).
 * @property {boolean|string|function} checkbox Display checkboxes to allow selection (default: false).<br>
 *     Note that selection via the API or initialization data is still possible.<br>
 *     true: display a checkbox in front of the node<br>
 *     false: no checkbox (default)<br>
 *     "radio": display a radio button in front of the node. This does not modify the selection behavior.<br>
 *     function(event, data): callback returning true, false, or a string.<br>
 *     NOTE: changed with v2.23.
 * @property {boolean} checkboxAutoHide Hide checkboxes unless selected or hovered.<br>
 *     This mimics Windows File Explorer 'Use check boxes to select items' mode.<br>
 *     (default: false, requires checkboxes enabled).
 *     @since 2.31
 * @property {Integer} clickFolderMode Defines what happens, when the user click a folder node.<br>1:activate, 2:expand, 3:activate and expand, 4:activate/dblclick expands  (default: 4)
 * @property {object|array} columns Made available as `tree.columns`.<br>
 *     Recommended place to store shared data for column rendering.
 *     See also <a href="https://github.com/mar10/fancytree/wiki/ExtTable">table extension</a>.
 *     @since 2.27
 * @property {Integer} debugLevel 0..4 (null: use global setting $.ui.fancytree.debugLevel)
 * @property {function} defaultKey callback(node) is called for new nodes without a key. Must return a new unique key. (default null: generates default keys like that: "_" + counter)
 * @property {boolean} <del>enableAspx</del> Accept passing ajax data in a property named `d` (default: true).
 *     @deprecated Call `data.result = data.response.d` in the `postProcess`event instead
 * @property {boolean} escapeTitles Make sure all HTML tags are escaped (default: false).
 * @property {string[]} extensions List of active extensions (default: [])
 * @property {boolean} focusOnSelect Set focus when node is checked by a mouse click (default: false)
 * @property {boolean} generateIds Add `id="..."` to node markup (default: false).
 * @property {boolean|function} icon Display node icons (default: true)<br>
 *     true: use default icons, depending on `node.folder` and `node.expanded`<br>
 *     false: hide icons<br>
 *     function(event, data): callback returning true, false, object, or a string.<br>
 *     NOTE: changed with v2.14.<br>
 *     See also <a href="https://github.com/mar10/fancytree/wiki#dynamic-options">dynamic options</a>.<br>
 *     See the node option of the same name for an explanation of possible string values.
 * @property {boolean} <del>icons</del> @deprecated use `icon` instead
 * @property {boolean|function} iconTooltip Add a `title` attribute to the node's icon span markup,
 *     thus enabling a tooltip (default: false).<br>
 *     See `tree.tooltip` option for details.
 *     See also <a href="https://github.com/mar10/fancytree/wiki#dynamic-options">dynamic options</a>.
 *     @since 2.27
 * @property {string} idPrefix prefix used to generate node markup ID attributes (default: "ft_", requires generateIds to be set)
 * @property {string} imagePath Path to a folder containing icons (default: null, using 'skin/' subdirectory).
 * @property {boolean} keyboard Support keyboard navigation (default: true).
 * @property {string} keyPathSeparator (default: "/")
 * @property {Integer} minExpandLevel 2: top-level nodes are not collapsible (default: 1)
 * @property {boolean|string|function} nodata Display a special message when the tree loader
 *     returned no data (default: true)
 *     @since 2.30
 * @property {boolean} quicksearch navigate to next node by typing the first letters (default: false)
 * @property {object} scrollOfs optional margins for node.scrollIntoView() (default: {top: 0, bottom: 0})
 * @property {jQuery} scrollParent scrollable container for node.scrollIntoView() (default: $container)
 * @property {Integer} selectMode 1:single, 2:multi, 3:multi-hier (default: 2)
 * @property {any} source Used to Initialize the tree.
 * @property {object} strings Translation table<br>
 *     default: `{loading: "Loading...", loadError: "Load error!", moreData: "More...", noData: "No data."}`
 * @property {boolean} <del>tabbable</del> @deprecated use `tabindex` instead
 * @property {string} tabindex Add tabindex attribute to container, so tree can be reached using TAB (default: "0")<br>
 *     "0": Tree control can be reached using TAB keys<br>
 *     "-1": Tree control cannot be reached using TAB keys, but may receive keyboard focusable using mouse clicks or JavaScript<br>
 *     "": Tree control is not tabbable nor may it receive keyboard focus.
 * @property {boolean} titlesTabbable Add tabindex='0' to node title span, so it can receive keyboard focus
 * @property {object} toggleEffect Animation options, false:off
 *     (default: { effect: "slideToggle", duration: 200 })
 * @property {boolean|function} tooltip Add a `title` attribute to the node's title span markup,
 *     thus enabling a tooltip (default: false).<br>
 *     false: No automatic tooltip (but still honor `node.tooltip` attribute)<br>
 *     true:  Use `node.title` as tooltip<br>
 *     function:  A `callback(event, data)<br>
 *     Note: If a node has the `node.tooltip` attribute set, this will take precedence.<br>
 *     See also <a href="https://github.com/mar10/fancytree/wiki#dynamic-options">dynamic options</a>.<br>
 *     Note: If a separate tooltip widget is used, it may be more efficient to use that widget
 *     API instead, instead of duplicating tree markup.
 *     (<a href="http://api.jqueryui.com/tooltip/#option-content">For example jQuery UI Tooltip</a>.)
 * @property {string} treeId optional fixed tree id and namespace (default: null)<br>
 *     By default, the `tree._id` is an integer, starting as `1` and automatically
 *     incremented every time a new Fancytree is instantiated.<br>
 *     The `tree._id` is used to define the hidden root node's id, persistence keys, form control ids,
 *     and can be passed to `$.ui.fancytree.getTree()`.<br>
 *     Also, `tree._ns` is calculated as `".fancytree-" + tree._id` and used for namespaced events.
 *     @since 2.31
 * @property {object} types Made available as `tree.types`.<br>
 *     Shared data for nodes with the same `node.type` attribute.
 *     See also <a href="https://github.com/mar10/fancytree/wiki/TutorialNodeTypes">node types</a>.
 *     @since 2.27
 * @property {boolean|function} unselectable (<a href="https://github.com/mar10/fancytree/wiki#dynamic-options">dynamic option</a>)
 *     Prevent (de-)selection using mouse or keyboard.<br>
 *     Note: This node can still be (de)selected by status propagation in selectMode 3.
 *     (Set `unselectableStatus` to prevent this.)
 * @property {boolean|function} unselectableIgnore (<a href="https://github.com/mar10/fancytree/wiki#dynamic-options">dynamic option</a>)
 *     Ignore this node when calculating the `partsel` status of parent nodes in selectMode 3 propagation.<br>
 *     If defined, `unselectable: true` is implied.
 * @property {boolean|function} unselectableStatus (<a href="https://github.com/mar10/fancytree/wiki#dynamic-options">dynamic option</a>)
 *     Use this as constant `selected` value (overriding selectMode 3 propagation).<br>
 *     If defined, `unselectable: true` is implied.
 */
var FancytreeOptions = {};

/** Fancytree events
 * @name FancytreeEvents
 * @type {Object}
 *
 * @description
 * Events are called like this:
 *    `CALLBACK_NAME(event, data)`
 * where `event` is a <a href="http://api.jquery.com/category/events/event-object">jQuery Event</a>
 * object and `data` is of type {@link EventData}.<br>
 * The `this` context is set to the tree's HTMLDivElement.
 *
 * @see {@link http://api.jquery.com/category/events/event-object|jQuery Event}
 * @see EventData
 *
 * @example $("#tree").fancytree({
 *     activate: function(event, data){
 *         var node = data.node;
 *     }
 * });
 *
 * @property {function} activate `data.node` was activated
 * @property {function} beforeActivate `data.node` is about to be (de)activated. Current status is `data.node.isActive()`. Return `false` to prevent default processing.
 * @property {function} beforeExpand `data.node` is about to be collapsed/expanded. Current status is `data.node.isExpanded()`. Return `false` to prevent default processing.
 * @property {function} beforeRestore ext-persist is about to restore the previous state. Return `false` to prevent default processing.
 * @property {function} beforeSelect `data.node` is about to be (de)selected. Current status is `data.node.isSelected()`. Return `false` to prevent default processing.
 * @property {function} beforeUpdateViewport ext-grid is about to redraw the tree.viewport.<br>
 *     `data.next`: viewport settings that will be applied.<br>
 *     `data.diff`: changes to the current `tree.viewport`, e.g. start offset.<br>
 *     `data.reason`: a string describing the type of change.<br>
 *     `data.scrollOnly`: true if only the `start` value has changed.<br>
 *     Modify `next` or return `false` to prevent default processing.
 * @property {function} blur `data.node` lost keyboard focus
 * @property {function} blurTree `data.tree` lost keyboard focus
 * @property {function} click `data.node` was clicked. `data.targetType` contains the region ("checkbox", "expander", "icon", "prefix", "title"). Return `false` to prevent default processing, i.e. activating, expanding, selecting, etc.
 * @property {function} clickPaging `data.node` is a 'paging' status node and was activated. Use data.node.replaceWith() to load additional nodes.
 * @property {function} collapse `data.node` was collapsed
 * @property {function} create Widget was created.<br>
 *     Source data may *not* be loaded or rendered yet:
 *     see also the `init` event, which is fired later.<br>
 *     Note: called only once, but not when re-initialized.<br>
 * @property {function} createNode Allow tweaking and binding, after node was created for the first time (NOTE: this event is only available as callback, but not for bind())
 * @property {function} dblclick `data.node` was double-clicked. `data.targetType` contains the region ("checkbox", "expander", "icon", "prefix", "title"). Return `false` to prevent default processing, i.e. expanding, etc.
 * @property {function} deactivate `data.node` was deactivated
 * @property {function} defaultGridAction (used by ext-aria) The user hit enter on the active row or cell.<br>
 *     `data.activeTd` contains the currently active `<td>` element or null<br>
 *     `data.colIdx` contains the 0-based column index or -1
 * @property {function} enhanceTitle Allow extending the `<span class='fancytree-title'>` markup, for example by adding badges, ... (NOTE: this event is only available as callback, but not for bind())
 * @property {function} expand `data.node` was expanded
 * @property {function} focus `data.node` received keyboard focus
 * @property {function} focusTree `data.tree` received keyboard focus
 * @property {function} <del>iconClass</del> @deprecated use tree option `icon` instead.
 * @property {function} init Widget was (re-)initialized.<br>
 *     The tree widget was initialized, source data was loaded, visible nodes are rendered,
 *     selection propagation applied, and node activated.<br>
 *     `data.status` is false on load error.<br>
 *     Note: `preInit` is fired before nodes are loaded.<br>
 *     Note: if ext-persist is used, see also the `restore` event, which is fired later.
 * @property {function} keydown `data.node` received key. `event.which` contains the key. Return `false` to prevent default processing, i.e. navigation. Call `data.result = "preventNav";` to prevent navigation but still allow default handling inside embedded input controls.
 * @property {function} keypress (currently unused)
 * @property {function} lazyLoad `data.node` is a lazy node that is expanded for the first time. The new child data must be returned in the `data.result` property (see `source` option for available formats).
 * @property {function} loadChildren Node data was loaded, i.e. `node.nodeLoadChildren()` finished
 * @property {function} loadError A load error occurred. Return `false` to prevent default processing.
 * @property {function} modifyChild A <i>child</i> of `data.node` was added, removed, or otherwise modified<br>
 *     `data.operation` contains 'add', 'remove', 'rename', 'move', 'sort', 'data'<br>
 *     `data.childNode` contains the new, deleted, or modified child node if applicable<br>
 *     Note that this event is not necessarily fired for each single deleted or added node, when a hierarchy. was modified.<br>
 *     This event is only available as callback, but not for bind().
 * @property {function} postProcess Allows to modify the ajax response
 * @property {function} preInit Widget markup was created, but no data loaded yet.<br>
 *     @see init
 * @property {function} <del>removeNode</del> @deprecated use `modifyChild` with operation: 'remove' instead.
 * @property {function} renderColumns (used by table extension)
 * @property {function} renderStatusColumns (used by table extension)
 * @property {function} renderNode Allow tweaking after node state was rendered (NOTE: this event is only available as callback, but not for bind())
 * @property {function} renderTitle Allow replacing the `<span class='fancytree-title'>` markup (NOTE: this event is only available as callback, but not for bind())
 * @property {function} restore ext-persist has expanded, selected, and activated the previous state
 * @property {function} select `data.node` was (de)selected. Current status is `data.node.isSelected()`
 * @property {function} updateViewport ext-grid has redrawn the tree.viewport.<br>
 *     `data.prev`: viewport settings that were active before this update.<br>
 *     `data.diff`: changes to the current `tree.viewport`, e.g. start offset.<br>
 *     `data.reason`: a string describing the type of change.<br>
 *     `data.scrollOnly`: true if only the `start` value has changed.
 */
var FancytreeEvents = {};
