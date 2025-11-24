package com.webobjects.appserver;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import com.webobjects._ideservices._WOLaunchServices;
import com.webobjects.appserver._private.WOActionURL;
import com.webobjects.appserver._private.WOActiveImage;
import com.webobjects.appserver._private.WOAjaxRequestHandler;
import com.webobjects.appserver._private.WOApplet;
import com.webobjects.appserver._private.WOBody;
import com.webobjects.appserver._private.WOBrowser;
import com.webobjects.appserver._private.WOBundle;
import com.webobjects.appserver._private.WOCGIFormValues;
import com.webobjects.appserver._private.WOCheckBox;
import com.webobjects.appserver._private.WOClassicAdaptor;
import com.webobjects.appserver._private.WOComponentContent;
import com.webobjects.appserver._private.WOComponentDefinition;
import com.webobjects.appserver._private.WOComponentRequestHandler;
import com.webobjects.appserver._private.WOConditional;
import com.webobjects.appserver._private.WODefaultExceptions;
import com.webobjects.appserver._private.WODeployedBundle;
import com.webobjects.appserver._private.WODirectActionRequestHandler;
import com.webobjects.appserver._private.WOFileUpload;
import com.webobjects.appserver._private.WOForm;
import com.webobjects.appserver._private.WOFrame;
import com.webobjects.appserver._private.WOGenericContainer;
import com.webobjects.appserver._private.WOGenericElement;
import com.webobjects.appserver._private.WOHiddenField;
import com.webobjects.appserver._private.WOHttpIO;
import com.webobjects.appserver._private.WOHyperlink;
import com.webobjects.appserver._private.WOImage;
import com.webobjects.appserver._private.WOImageButton;
import com.webobjects.appserver._private.WOJavaScript;
import com.webobjects.appserver._private.WOParam;
import com.webobjects.appserver._private.WOPasswordField;
import com.webobjects.appserver._private.WOPopUpButton;
import com.webobjects.appserver._private.WOProjectBundle;
import com.webobjects.appserver._private.WOProperties;
import com.webobjects.appserver._private.WORadioButton;
import com.webobjects.appserver._private.WORecording;
import com.webobjects.appserver._private.WORepetition;
import com.webobjects.appserver._private.WOResetButton;
import com.webobjects.appserver._private.WOResourceRequestHandler;
import com.webobjects.appserver._private.WOResourceURL;
import com.webobjects.appserver._private.WOServerSessionStore;
import com.webobjects.appserver._private.WOStaticResourceRequestHandler;
import com.webobjects.appserver._private.WOString;
import com.webobjects.appserver._private.WOSubmitButton;
import com.webobjects.appserver._private.WOSwitchComponent;
import com.webobjects.appserver._private.WOText;
import com.webobjects.appserver._private.WOTextField;
import com.webobjects.appserver._private.WOURLFormatException;
import com.webobjects.appserver._private.WOUniqueIDGenerator;
import com.webobjects.appserver._private.WOXMLNode;
import com.webobjects.appserver.parser.woml.WOMLDefaultNamespaceProvider;
import com.webobjects.appserver.parser.woml.WOMLNamespaceProvider;
import com.webobjects.appserver.parser.woml.WOMLTemplateExtensions;
import com.webobjects.appserver.parser.woml.namespaces.WOMLDefaultNamespace;
import com.webobjects.appserver.parser.woml.namespaces.WOMLWebObjectsNamespace;
import com.webobjects.appserver.parser.woml.namespaces.WOMLWebObjectsQualifierNamespace;
import com.webobjects.eocontrol.EOAndQualifier;
import com.webobjects.eocontrol.EOEventCenter;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOGenericRecord;
import com.webobjects.eocontrol.EOGlobalID;
import com.webobjects.eocontrol.EOKeyComparisonQualifier;
import com.webobjects.eocontrol.EOKeyGlobalID;
import com.webobjects.eocontrol.EOKeyValueQualifier;
import com.webobjects.eocontrol.EONotQualifier;
import com.webobjects.eocontrol.EOObjectStore;
import com.webobjects.eocontrol.EOOrQualifier;
import com.webobjects.eocontrol.EOQualifierVariable;
import com.webobjects.eocontrol.EOSharedEditingContext;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.eocontrol.EOTemporaryGlobalID;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSBundle;
import com.webobjects.foundation.NSData;
import com.webobjects.foundation.NSDelayedCallbackCenter;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSForwardException;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSKeyValueCodingAdditions;
import com.webobjects.foundation.NSLog;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSMutableSet;
import com.webobjects.foundation.NSNotificationCenter;
import com.webobjects.foundation.NSPathUtilities;
import com.webobjects.foundation.NSProperties;
import com.webobjects.foundation.NSPropertyListSerialization;
import com.webobjects.foundation.NSTimestamp;
import com.webobjects.foundation.NSTimestampFormatter;
import com.webobjects.foundation._NSStringUtilities;
import com.webobjects.foundation._NSThreadsafeMutableDictionary;
import com.webobjects.foundation._NSThreadsafeMutableSet;
import com.webobjects.foundation._NSUtilities;

public class WOApplication
		implements NSKeyValueCoding, NSKeyValueCoding.ErrorHandling, NSKeyValueCodingAdditions, WOApplicationMBean {
	private final String _name;

	private String _agentID;

	private String _jmxDomain;

	private MBeanServer _mbs;

	private volatile WOSessionStore _sessionStore;

	private volatile WOStatisticsStore _statisticsStore;

	private final _NSThreadsafeMutableDictionary<String, WOComponentDefinition> _componentDefinitionCache;

	private final _NSThreadsafeMutableDictionary<String, WORequestHandler> _requestHandlers;

	private volatile WORequestHandler _defaultRequestHandler;

	private volatile WOResourceManager _resourceManager;

	private volatile WOAdaptor _defaultAdaptor;

	private NSTimestamp _runLoopDate;

	long _timeOut;

	protected boolean _cgiAdaptorURLParsed = false;

	public static final String CGIAdaptorURLProperty = "application.cgiAdaptorUrl";

	public static final String DocumentRootProperty = "application.documentRoot";

	public static final String DirectoryAliasProperty = "application.directoryAlias";

	@Deprecated
	private WOTimer _timer;

	Timer _applicationTimer;

	private _WORunLoop _currentRunLoop;

	protected volatile boolean _terminating;

	private final Object _globalLock;

	private final Object _recorder;

	private NSMutableArray<WOAdaptor> _adaptors;

	private InetAddress _hostAddress;

	public static URL[] _classPathURLs;

	public boolean _unsetHost = true;

	public static boolean _wasMainInvoked = false;

	private volatile int _activeSessionsCount;

	private volatile int _minimumActiveSessions;

	private volatile int _permanentCacheSize;

	private volatile int _pageCacheSize;

	private volatile int _pageFragmentCacheSize;

	private volatile boolean _refusingNewClients;

	private volatile boolean _dynamicLoadingEnabled;

	private volatile boolean _pageRefreshOnBacktrackEnabled;

	private boolean _isMultiThreaded;

	private boolean _allowsConcurrentRequestHandling;

	static _LifebeatThread _lifebeatThread;

	public static final String _adminRequestHandlerKey = "womp";

	private static WOApplication _WOApp;

	private static String _WOAppClassName;

	private static String _WOAppPackageName = "";

	private static Class<?> _theSessionClass = null;

	private static String[] _argv = null;

	private static int TheLoadedFrameworkCount = 0;

	private static NSMutableArray<String> TheComponentBearingFrameworks = null;

	private static boolean _checksForSpecialHeaders = false;

	protected static volatile long _TheLastApplicationAccessTime;

	static volatile boolean _IsEventLoggingEnabled = false;

	private static final String pageWithNameEvent = "pageWithName";

	private final boolean _inRapidTurnaroundMode;

	private final _NSThreadsafeMutableSet<String> _expectedLanguages = new _NSThreadsafeMutableSet<>(
			new NSMutableSet<>(16));

	public static _WOLaunchServices _launchServices = new _WOLaunchServices();

	public static final String ApplicationWillFinishLaunchingNotification = "ApplicationWillFinishLaunchingNotification";

	public static final String ApplicationDidFinishLaunchingNotification = "ApplicationDidFinishLaunchingNotification";

	public static final String ApplicationWillDispatchRequestNotification = "ApplicationWillDispatchRequestNotification";

	public static final String ApplicationDidDispatchRequestNotification = "ApplicationDidDispatchRequestNotification";

	private static final String _WORecordingClassName = "com.webobjects.appserver._private.WORecording";

	private static final String _WORecordingClassNameKey = "WORecordingClassName";

	private WOAssociationFactoryRegistry _associationFactoryRegistry;

	static {
		try {
			_NSUtilities.registerPackage("com.webobjects.appserver._private");
			_NSUtilities.registerPackage("com.webobjects.appserver");
			_NSUtilities.registerPackage("com.webobjects.eoaccess");
			_NSUtilities.registerPackage("com.webobjects.eocontrol");
			_NSUtilities.registerPackage("com.webobjects.woextensions");
			_NSUtilities.registerPackage("com.webobjects.examples.woexamples");
			try {
				final Class<?> kvcAccessorClass = Class.forName("KeyValueCodingProtectedAccessor");
				if (kvcAccessorClass != null) {
					_NSUtilities.setClassForName(kvcAccessorClass, "KeyValueCodingProtectedAccessor");
				}
			} catch (final ClassNotFoundException ex) {
			}
			_NSUtilities.setClassForName(EOGenericRecord.class, "EOGenericRecord");
			_NSUtilities.setClassForName(EOFetchSpecification.class, "EOFetchSpecification");
			_NSUtilities.setClassForName(EOGlobalID.class, "EOGlobalID");
			_NSUtilities.setClassForName(EOKeyGlobalID.class, "EOKeyGlobalID");
			_NSUtilities.setClassForName(EOSortOrdering.class, "EOSortOrdering");
			_NSUtilities.setClassForName(EOAndQualifier.class, "EOAndQualifier");
			_NSUtilities.setClassForName(EOKeyValueQualifier.class, "EOKeyValueQualifier");
			_NSUtilities.setClassForName(EONotQualifier.class, "EONotQualifier");
			_NSUtilities.setClassForName(EOOrQualifier.class, "EOOrQualifier");
			_NSUtilities.setClassForName(EOKeyComparisonQualifier.class, "EOKeyComparisonQualifier");
			_NSUtilities.setClassForName(EOQualifierVariable.class, "EOQualifierVariable");
			_NSUtilities.setClassForName(WOApplication.class, "WOApplication");
			_NSUtilities.setClassForName(WOClassicAdaptor.class, "WODefaultAdaptor");
			_NSUtilities.setClassForName(WOComponentRequestHandler.class, "WOComponentRequestHandler");
			_NSUtilities.setClassForName(WODirectActionRequestHandler.class, "WODirectActionRequestHandler");
			_NSUtilities.setClassForName(WODisplayGroup.class, "WODisplayGroup");
			_NSUtilities.setClassForName(WOAdminAction.class, "WOAdminAction");
			_NSUtilities.setClassForName(WOServerSessionStore.class, "WOServerSessionStore");
			_NSUtilities.setClassForName(WOContext.class, "WOContext");
			_NSUtilities.setClassForName(WOActionURL.class, "WOActionURL");
			_NSUtilities.setClassForName(WOActiveImage.class, "WOActiveImage");
			_NSUtilities.setClassForName(WOApplet.class, "WOApplet");
			_NSUtilities.setClassForName(WOBody.class, "WOBody");
			_NSUtilities.setClassForName(WOBrowser.class, "WOBrowser");
			_NSUtilities.setClassForName(WOCheckBox.class, "WOCheckBox");
			_NSUtilities.setClassForName(WOComponentContent.class, "WOComponentContent");
			_NSUtilities.setClassForName(WOConditional.class, "WOConditional");
			_NSUtilities.setClassForName(WOFileUpload.class, "WOFileUpload");
			_NSUtilities.setClassForName(WOForm.class, "WOForm");
			_NSUtilities.setClassForName(WOFrame.class, "WOFrame");
			_NSUtilities.setClassForName(WOGenericContainer.class, "WOGenericContainer");
			_NSUtilities.setClassForName(WOGenericElement.class, "WOGenericElement");
			_NSUtilities.setClassForName(WOHiddenField.class, "WOHiddenField");
			_NSUtilities.setClassForName(WOHyperlink.class, "WOHyperlink");
			_NSUtilities.setClassForName(WOImage.class, "WOImage");
			_NSUtilities.setClassForName(WOImageButton.class, "WOImageButton");
			_NSUtilities.setClassForName(WOJavaScript.class, "WOJavaScript");
			_NSUtilities.setClassForName(WOParam.class, "WOParam");
			_NSUtilities.setClassForName(WOPasswordField.class, "WOPasswordField");
			_NSUtilities.setClassForName(WOPopUpButton.class, "WOPopUpButton");
			_NSUtilities.setClassForName(WORadioButton.class, "WORadioButton");
			_NSUtilities.setClassForName(WORepetition.class, "WORepetition");
			_NSUtilities.setClassForName(WOResetButton.class, "WOResetButton");
			_NSUtilities.setClassForName(WOResourceURL.class, "WOResourceURL");
			_NSUtilities.setClassForName(WOString.class, "WOString");
			_NSUtilities.setClassForName(WOSubmitButton.class, "WOSubmitButton");
			_NSUtilities.setClassForName(WOSwitchComponent.class, "WOSwitchComponent");
			_NSUtilities.setClassForName(WOText.class, "WOText");
			_NSUtilities.setClassForName(WOTextField.class, "WOTextField");
			_NSUtilities.setClassForName(WOXMLNode.class, "WOXMLNode");
			_NSUtilities.setClassForName(WOServerSessionStore.class, "WOServerSessionStore");
			EOEventCenter.registerEventClass(Event.class, new _EventLoggingEnabler());
		} catch (final Exception exception) {
			NSLog.err.appendln("<WOApplication> Exception during static initialization: " + exception.toString());
			if (NSLog.debugLoggingAllowedForLevel(1)) {
				NSLog.debug.appendln(exception);
			}
			System.exit(1);
		}
	}

	protected class TimeoutTask extends TimerTask {
		public void start() {
			applicationTimer().schedule(this, (long) (timeOut() * 1000.0D));
		}

		@Override
		public void run() {
			final long inactivity = System.currentTimeMillis() - WOApplication._TheLastApplicationAccessTime;
			if (inactivity >= timeOut()) {
				terminate();
			} else {
				applicationTimer().schedule(this, (long) (timeOut() * 1000.0D) - inactivity);
			}
		}

		@Override
		public boolean cancel() {
			return super.cancel();
		}
	}

	private class QuitTask extends TimerTask {
		public void start() {
			applicationTimer().schedule(this, 0L);
		}

		@Override
		public void run() {
			_WORunLoop.currentRunLoop().stopNow();
		}
	}

	public static class Event extends WOEvent {
		private static final long serialVersionUID = 9103314272492083747L;

		@Override
		public String displayComponentName() {
			return "WOEventRow";
		}

		@Override
		public String comment() {
			final Object info = info();
			return info instanceof String ? (String) info : super.comment();
		}
	}

	public static void main(final String[] argv) {
		main(argv, WOApplication.class);
	}

	public static void main(final String[] argv, final Class<? extends WOApplication> applicationClass) {
		try {
			_wasMainInvoked = true;
			_argv = argv;
			if (applicationClass == null) {
				throw new IllegalArgumentException("No application class specified");
			}
			if (applicationClass.getClassLoader().getClass().equals(Class.forName("java.net.URLClassLoader"))) {
				_classPathURLs = ((URLClassLoader) applicationClass.getClassLoader()).getURLs();
			}
			final WOApplication application = applicationClass.newInstance();
			application.run();
		} catch (final Throwable throwable) {
			try {
				NSLog.err.appendln("A fatal exception occurred: " + throwable.getMessage());
				NSLog.err.appendln(throwable);
				if (_lifebeatThread != null) {
					_lifebeatThread.sendMessage(_lifebeatThread._willCrash);
				}
			} finally {
				System.exit(1);
			}
		}
		System.exit(0);
	}

	static void _setApplication(final WOApplication anApplication) {
		_WOApp = anApplication;
		if (anApplication != null) {
			_WOAppClassName = anApplication.getClass().getName();
		}
	}

	public static WOApplication application() {
		return _WOApp;
	}

	@Deprecated
	public static void primeApplication(final String mainBundlePath, final String nameOfApplicationSubclass) {
		if (_WOApp == null) {
			NSBundle mainBundle = null;
			if (mainBundlePath != null) {
				mainBundle = NSBundle.bundleWithPath(mainBundlePath);
			}
			if (mainBundle == null) {
				mainBundle = NSBundle.bundleForName("JavaWebObjects.framework");
			}
			NSBundle._setMainBundle(mainBundle);
			String appName = NSProperties.getProperty(WOProperties._ApplicationNameKey);
			if (appName == null) {
				appName = NSBundle.mainBundle().name();
			}
			if (NSLog.out instanceof NSLog.PrintStreamLogger) {
				((NSLog.PrintStreamLogger) NSLog.out)._setPrefixInfo(appName);
			}
			if (NSLog.err instanceof NSLog.PrintStreamLogger) {
				((NSLog.PrintStreamLogger) NSLog.err)._setPrefixInfo(appName);
			}
			if (NSLog.debug instanceof NSLog.PrintStreamLogger) {
				((NSLog.PrintStreamLogger) NSLog.debug)._setPrefixInfo(appName);
			}
			Class<?> appClass = null;
			if (nameOfApplicationSubclass != null) {
				appClass = _NSUtilities.classWithName(nameOfApplicationSubclass);
			}
			if (appClass == null) {
				appClass = _NSUtilities.classWithName("com.webobjects.appserver.WOApplication");
			}
			final WOApplication application = (WOApplication) _NSUtilities.instantiateObject(appClass, null, null, true,
					true);
			_WOAppPackageName = _NSStringUtilities.stringByDeletingLastComponent(nameOfApplicationSubclass, '.');
			if (_WOAppPackageName == null || _WOAppPackageName.length() == 0) {
				NSLog.err.appendln(
						"<WOApplication>.primeApplication: Application class is not in a package. This could cause problems with some appservers.");
			}
			NSLog.debug.appendln("<WOApplication>.primeApplication: The Application name is " + application.name());
			_WOApp.run();
		}
	}

	public static void primeApplication(final String mainBundleName, final URL mainBundlePathURL,
			final String nameOfApplicationSubclass) {
		String mainBundlePath = null;
		if (mainBundlePathURL != null && "file".equals(mainBundlePathURL.getProtocol())) {
			mainBundlePath = mainBundlePathURL.getPath();
		}
		if (mainBundleName != null) {
			NSBundle bundle = NSBundle.bundleForName(mainBundleName);
			if (bundle != null) {
				mainBundlePath = bundle.bundlePathURL().getPath();
			} else {
				bundle = NSBundle._appBundleForName(mainBundleName);
				if (bundle != null) {
					mainBundlePath = bundle.bundlePathURL().getPath();
				}
			}
		}
		primeApplication(mainBundlePath, nameOfApplicationSubclass);
	}

	private WOMLNamespaceProvider _namespaceProvider = createDefaultNamespaceProvider();

	private final Class<?>[] _createContextForRequestParams;

	public boolean wasMainInvoked() {
		return _wasMainInvoked;
	}

	@Deprecated
	public EOSharedEditingContext sharedEditingContext() {
		return EOSharedEditingContext.defaultSharedEditingContext();
	}

	private boolean _initializeRapidTurnaroundMode() {
		boolean inRapidTurnaroundMode = resourceManager()._appProjectBundle() instanceof WOProjectBundle;
		if (!inRapidTurnaroundMode) {
			final NSArray frameworkProjectBundles = resourceManager()._frameworkProjectBundles();
			final int count = frameworkProjectBundles.count();
			for (int i = 0; i < count; i++) {
				if (frameworkProjectBundles.objectAtIndex(i) instanceof WOProjectBundle) {
					inRapidTurnaroundMode = true;
					break;
				}
			}
		}
		return inRapidTurnaroundMode;
	}

	@Override
	public String toString() {
		return "<WOApplication: name=" + _name + " adaptors=" + _adaptors + " sessionStore=" + _sessionStore
				+ " pageCacheSize=" + _pageCacheSize + " permanentCacheSize=" + _permanentCacheSize
				+ " pageRecreationEnabled=" + _isPageRecreationEnabled() + " pageRefreshOnBacktrackEnabled="
				+ isPageRefreshOnBacktrackEnabled() + " componentDefinitionCache=" + _componentDefinitionCache
				+ " caching=" + isCachingEnabled() + " terminating=" + isTerminating() + " timeOut(sec)=" + timeOut()
				+ " dynamicLoadingEnabled=" + _isDynamicLoadingEnabled() + ">";
	}

	public String path() {
		return _resourceManager._appProjectBundle().bundlePath();
	}

	public String baseURL() {
		return applicationBaseURL();
	}

	@Override
	public String getBaseURL() {
		return baseURL();
	}

	public String number() {
		return "-1";
	}

	public String name() {
		return _name;
	}

	@Override
	public String getName() {
		return name();
	}

	@Override
	public String getWebObjectsVersion() {
		String version = "Not Available";
		final NSBundle wofBundle = NSBundle.bundleForName("JavaWebObjects");
		if (wofBundle != null) {
			final NSDictionary<String, Object> infoPlist = wofBundle._infoDictionary();
			final String aVersion = (String) infoPlist.valueForKey("CFBundleShortVersionString");
			if (aVersion != null && aVersion.trim().length() > 0) {
				version = aVersion;
			}
		}
		return version;
	}

	public HashMap<String, String> getFrameworkVersions() {
		final NSArray<NSBundle> frameworkBundles = NSBundle.frameworkBundles();
		final HashMap<String, String> versions = new HashMap<>();
		for (final NSBundle currBundle : frameworkBundles) {
			final NSDictionary<String, Object> infoPlist = currBundle._infoDictionary();
			if (infoPlist != null) {
				versions.put(currBundle.name(), (String) infoPlist.valueForKey("CFBundleShortVersionString"));
				continue;
			}
//			if (currBundle.isJar()) {
//				try {
//					Manifest manifest = currBundle._jarFile().getManifest();
//					if (manifest != null) {
//						Attributes attr = manifest.getAttributes("Implementation-Version");
//						versions.put(currBundle.name(), attr.getValue("Implementation-Version"));
//					}
//				} catch (Exception e) {
//				}
//			}
		}
		return versions;
	}

	@Override
	public ArrayList<String> getClassPaths() {
		final ArrayList<String> paths = new ArrayList<>();
		if (_classPathURLs != null) {
			for (final URL _classPathURL : _classPathURLs) {
				paths.add(_classPathURL.toExternalForm());
			}
		}
		return paths;
	}

	private boolean _runOnce() {
		if (isTerminating()) {
			return false;
		}
		_currentRunLoop.runBeforeDate(_runLoopDate);
		return true;
	}

	public _WORunLoop _runLoop() {
		return _currentRunLoop;
	}

	public void terminate() {
		_terminating = true;
		if (wasMainInvoked()) {
			new QuitTask().start();
		}
	}

	public void _quitTimer() {
	}

	Timer applicationTimer() {
		if (_applicationTimer == null) {
			_applicationTimer = new Timer(true);
		}
		return _applicationTimer;
	}

	public boolean isTerminating() {
		return _terminating;
	}

	@Override
	public boolean getIsTerminating() {
		return isTerminating();
	}

	public void setTimeOut(final double aTimeInterval) {
		long timeout;
		if (aTimeInterval == 0.0D) {
			timeout = NSTimestamp.DistantFuture.getTime() - System.currentTimeMillis();
		} else {
			timeout = (long) (aTimeInterval * 1000.0D);
		}
		synchronized (this) {
			_timeOut = timeout;
			new TimeoutTask().start();
		}
	}

	public double timeOut() {
		return _timeOut / 1000.0D;
	}

	@Override
	public double getTimeOut() {
		return timeOut();
	}

	@Deprecated
	private synchronized void _scheduleApplicationTimerForTimeInterval(final long aTimeInterval) {
		if (_timer != null) {
			_timer.invalidate();
		}
		_timer = new WOTimer(aTimeInterval, this, "_terminateOrResetTimer", null, Object.class, false);
		_timer.schedule();
	}

	@Deprecated
	public void _terminateOrResetTimer(final Object sender) {
		final long aTimeIntervalSinceReferenceDate = System.currentTimeMillis();
		if (aTimeIntervalSinceReferenceDate - _TheLastApplicationAccessTime >= _timeOut) {
			terminate();
		} else {
			_scheduleApplicationTimerForTimeInterval(
					_timeOut - aTimeIntervalSinceReferenceDate - _TheLastApplicationAccessTime);
		}
	}

	public void run() {
		final NSArray<WOAdaptor> adaptors = adaptors();
		final int adaptorCount = adaptors.count();
		NSNotificationCenter.defaultCenter().postNotification("ApplicationWillFinishLaunchingNotification", this);
		for (int adaptorIndex = 0; adaptorIndex < adaptorCount; adaptorIndex++) {
			adaptors.objectAtIndex(adaptorIndex).registerForEvents();
		}
		if (wasMainInvoked()) {
			_runLoopDate = NSTimestamp.DistantFuture;
			_launchServices._openInitialURL();
			NSNotificationCenter.defaultCenter().postNotification("ApplicationDidFinishLaunchingNotification", this);
			NSLog.debug.appendln("Waiting for requests...");
		} else {
			_runLoopDate = NSTimestamp.DistantPast;
			_launchServices._openInitialURL();
			_terminating = true;
		}
		do {

		} while (_runOnce());
		final Thread[] workers = new Thread[adaptorCount];
		final NSArray<WOAdaptor> finalAdaptors = adaptors;
		int i;
		for (i = 0; i < adaptorCount; i++) {
			final int j = i;
			final Runnable work = () -> finalAdaptors.objectAtIndex(j).unregisterForEvents();
			workers[j] = new Thread(work);
			workers[j].start();
		}
		try {
			for (i = 0; i < workers.length; i++) {
				workers[i].join();
			}
		} catch (final InterruptedException e) {
			NSLog._conditionallyLogPrivateException(e);
		}
		if (_lifebeatThread != null) {
			_lifebeatThread.sendMessage(_lifebeatThread._willStop);
		}
	}

	public WOAdaptor adaptorWithName(final String aClassName, final NSDictionary<String, Object> anArgsDictionary) {
		if (aClassName == null) {
			throw new InstantiationError("<" + _WOAppClassName + ">: Name missing for adaptor creation.");
		}
		Class<?> anAdaptorClass = _NSUtilities.classWithName("com.webobjects.appserver.nioadaptor.".concat(aClassName));
		if (anAdaptorClass == null) {
			anAdaptorClass = _NSUtilities.classWithName("com.webobjects.appserver._private.".concat(aClassName));
			if (anAdaptorClass == null) {
				anAdaptorClass = _NSUtilities.classWithName(aClassName);
				if (anAdaptorClass == null) {
					throw new InstantiationError(
							"<" + _WOAppClassName + ">: Unable to locate class named: " + aClassName + " .");
				}
			}
		}
		final Class<?>[] params = WOAdaptor._ConstructorParametersTypes;
		final Object[] arguments = { aClassName, anArgsDictionary };
		final WOAdaptor anAdaptorInstance = (WOAdaptor) _NSUtilities.instantiateObject(anAdaptorClass, params,
				arguments, true,
				isDebuggingEnabled());
		if (anAdaptorInstance.dispatchesRequestsConcurrently()) {
			_isMultiThreaded = true;
		}
		return anAdaptorInstance;
	}

	public NSArray<WOAdaptor> adaptors() {
		if (_adaptors != null) {
			return _adaptors;
		}
		return NSArray.emptyArray();
	}

	public WOAdaptor defaultAdaptor() {
		if (_defaultAdaptor == null) {
			final NSArray<WOAdaptor> adaptors = adaptors();
			if (adaptors.size() > 0) {
				_defaultAdaptor = adaptors.objectAtIndex(0);
			}
		}
		return _defaultAdaptor;
	}

	public String getDefaultAdaptor() {
		final WOAdaptor adaptor = defaultAdaptor();
		return adaptor != null ? adaptor.getClass().getName() : "";
	}

	private NSMutableDictionary<String, Object> _argsDictionary() {
		final NSMutableDictionary<String, Object> anArgsDict = new NSMutableDictionary<>();
		anArgsDict.takeValueForKey(host(), WOProperties._HostKey);
		anArgsDict.setObjectForKey(port(), WOProperties._PortKey);
		anArgsDict.setObjectForKey(adaptor(), WOProperties._AdaptorKey);
		anArgsDict.setObjectForKey(workerThreadCount(), WOProperties._WorkerThreadCountKey);
		anArgsDict.setObjectForKey(workerThreadCountMin(), WOProperties._WorkerThreadCountMinKey);
		anArgsDict.setObjectForKey(workerThreadCountMax(), WOProperties._WorkerThreadCountMaxKey);
		anArgsDict.setObjectForKey(listenQueueSize(), WOProperties._ListenQueueSizeKey);
		anArgsDict.setObjectForKey(maxSocketIdleTime(), WOProperties._MaxSocketIdleTimeKey);
		return anArgsDict;
	}

	private void _initAdaptors() {
		final NSArray<NSDictionary<String, Object>> anOtherAdaptorsArray = additionalAdaptors();
		_adaptors = new NSMutableArray<>(anOtherAdaptorsArray.count() + 1);
		final NSMutableDictionary<String, Object> nSMutableDictionary = _argsDictionary();
		String anAdaptorName = (String) nSMutableDictionary.objectForKey(WOProperties._AdaptorKey);
		WOAdaptor anAdaptor = adaptorWithName(anAdaptorName, nSMutableDictionary);
		_adaptors.addObject(anAdaptor);
		for (final NSDictionary<String, Object> nSDictionary : anOtherAdaptorsArray) {
			anAdaptorName = (String) nSDictionary.objectForKey(WOProperties._AdaptorKey);
			anAdaptor = adaptorWithName(anAdaptorName, nSDictionary);
			_adaptors.addObject(anAdaptor);
		}
	}

	class _LifebeatThread extends Thread {
		private InetAddress _localAddress = null;

		private final String _localhostName;

		private final int _lifebeatDestinationPort;

		private final long _lifebeatInterval;

		private String _appName = null;

		private int _appPort = -1;

		private int _deathCounter = 0;

		private Socket lifebeatSocket = null;

		private OutputStream lifebeatOS = null;

		private InputStream lifebeatIS = null;

		private final byte[] lifebeatResponseBuffer = new byte["HTTP/1.X XXX".length()
				+ WOHttpIO.URIResponseString.length()
				+ "\r\n\r\n".length()];

		private DatagramSocket datagramSocket = null;

		private byte[] _versionRequest = null;

		byte[] _mbuffer = new byte[1000];

		DatagramPacket incomingPacket;

		DatagramPacket outgoingPacket;

		private byte[] _hasStarted = null;

		private byte[] _lifebeat = null;

		byte[] _willStop = null;

		byte[] _willCrash = null;

		_LifebeatThread(final String anAppName, final int anAppPort, final InetAddress anAppHost,
				final int aLifebeatPort,
				final long aLifebeatInterval) {
			NSLog.debug.appendln("Creating LifebeatThread now with: " + anAppName + " " + anAppPort + " " + anAppHost
					+ " " + aLifebeatPort + " " + aLifebeatInterval);
			_lifebeatDestinationPort = aLifebeatPort;
			_lifebeatInterval = aLifebeatInterval;
			_appName = anAppName;
			_appPort = anAppPort;
			_localAddress = anAppHost;
			_localhostName = anAppHost.getHostName();
		}

		private void initMessages() {
			final String preString = "GET /cgi-bin/WebObjects/wotaskd.woa/wlb?";
			final String postString = "&" + _appName + "&" + _localhostName + "&" + _appPort + " HTTP/1.1\r\n\r\n";
			final String versionString = "womp://queryVersion";
			try {
				_hasStarted = (preString + "hasStarted" + postString).getBytes("UTF8");
				_lifebeat = (preString + "lifebeat" + postString).getBytes("UTF8");
				_willStop = (preString + "willStop" + postString).getBytes("UTF8");
				_willCrash = (preString + "willCrash" + postString).getBytes("UTF8");
				_versionRequest = versionString.getBytes("UTF8");
			} catch (final UnsupportedEncodingException uee) {
				NSLog._conditionallyLogPrivateException(uee);
				_hasStarted = (preString + "hasStarted" + postString).getBytes();
				_lifebeat = (preString + "lifebeat" + postString).getBytes();
				_willStop = (preString + "willStop" + postString).getBytes();
				_willCrash = (preString + "willCrash" + postString).getBytes();
				_versionRequest = versionString.getBytes();
			}
		}

		void sendMessage(final byte[] aMessage) {
			if (aMessage == null) {
				return;
			}
			try {
				if (lifebeatSocket == null) {
					if (NSLog.debugLoggingAllowedForLevelAndGroups(3, 4194304L)) {
						NSLog.debug.appendln("Creating new lifebeat socket");
					}
					lifebeatSocket = new Socket(_localAddress, _lifebeatDestinationPort, _localAddress, 0);
					lifebeatSocket.setTcpNoDelay(true);
					lifebeatSocket.setSoLinger(false, 0);
					lifebeatIS = lifebeatSocket.getInputStream();
					lifebeatOS = lifebeatSocket.getOutputStream();
				}
				lifebeatOS.write(aMessage);
				lifebeatOS.flush();
				int fetched = 0;
				int thisFetch = -1;
				while (fetched < lifebeatResponseBuffer.length) {
					thisFetch = lifebeatIS.read(lifebeatResponseBuffer, fetched,
							lifebeatResponseBuffer.length - fetched);
					if (thisFetch != -1) {
						fetched += thisFetch;
					}
				}
				if (thisFetch == -1 || lifebeatResponseBuffer[9] == 52) {
					_closeLifebeatSocket();
				} else if (lifebeatResponseBuffer[9] == 53) {
					try {
						NSLog.err.appendln("Force Quit received. Exiting now...");
						lifebeatSocket = new Socket(_localAddress, _lifebeatDestinationPort, _localAddress, 0);
						lifebeatOS = lifebeatSocket.getOutputStream();
						lifebeatOS.write(WOApplication._lifebeatThread._willCrash);
						lifebeatOS.flush();
						_closeLifebeatSocket();
					} finally {
						System.exit(1);
					}
				} else {
					_deathCounter = 0;
				}
			} catch (final IOException e) {
				if (NSLog.debugLoggingAllowedForLevelAndGroups(3, 4194304L)) {
					NSLog.debug.appendln("Exception sending lifebeat to wotaskd: " + e);
				}
				_closeLifebeatSocket();
			}
		}

		private void _closeLifebeatSocket() {
			lifebeatOS = null;
			lifebeatIS = null;
			if (lifebeatSocket != null) {
				try {
					lifebeatSocket.close();
				} catch (final IOException ioe) {
					if (NSLog.debugLoggingAllowedForLevelAndGroups(2, 4194304L)) {
						NSLog.debug.appendln("Exception closing lifebeat socket: " + ioe);
					}
				}
				lifebeatSocket = null;
			}
			_deathCounter++;
		}

		void udpMessage() {
			try {
				datagramSocket.send(outgoingPacket);
				incomingPacket.setLength(_mbuffer.length);
				datagramSocket.receive(incomingPacket);
				final String reply = _NSStringUtilities.stringForBytes(incomingPacket.getData(), "UTF-8");
				if (reply.startsWith("womp")) {
					_deathCounter = 0;
				}
			} catch (final Throwable e) {
				if (NSLog.debugLoggingAllowedForLevelAndGroups(3, 4194304L)) {
					NSLog.debug.appendln("Exception checking for wotaskd using UDP: " + e);
				}
			}
		}

		@Override
		public void run() {
			if (_localAddress == null) {
				return;
			}
			while (_appPort == -1 && adaptors().count() != 0) {
				try {
					Thread.sleep(5000L);
				} catch (final InterruptedException ex) {
					NSLog._conditionallyLogPrivateException(ex);
				}
				_appPort = adaptors().objectAtIndex(0).port();
			}
			initMessages();
			boolean _noUDPSocket = false;
			try {
				datagramSocket = new DatagramSocket(0, _localAddress);
				datagramSocket.setSoTimeout(5000);
				outgoingPacket = new DatagramPacket(_versionRequest, _versionRequest.length, _localAddress,
						_lifebeatDestinationPort);
				incomingPacket = new DatagramPacket(_mbuffer, _mbuffer.length);
			} catch (final SocketException e) {
				NSLog.err.appendln("<_LifebeatThread> Exception creating datagramSocket: " + e);
				_noUDPSocket = true;
			}
			sendMessage(_hasStarted);
			try {
				Thread.sleep(_lifebeatInterval);
			} catch (final InterruptedException ex) {
				NSLog._conditionallyLogPrivateException(ex);
			}
			while (true) {
				if (_deathCounter < 10 || _noUDPSocket) {
					sendMessage(_lifebeat);
				} else {
					udpMessage();
				}
				try {
					Thread.sleep(_lifebeatInterval);
				} catch (final InterruptedException ex) {
					NSLog._conditionallyLogPrivateException(ex);
				}
			}
		}
	}

	protected void _setAllowsCacheControlHeader(final boolean aBool) {
		WOProperties.TheAllowsCacheControlHeaderFlag = aBool;
		WOProperties.isTheAllowsCacheControlHeaderFlagSet = true;
	}

	protected boolean _allowsCacheControlHeader() {
		if (!WOProperties.isTheAllowsCacheControlHeaderFlagSet) {
			final String aValue = NSProperties.getProperty(WOProperties._AllowsCacheControlHeaderKey);
			_setAllowsCacheControlHeader(Boolean.parseBoolean(aValue));
		}
		return WOProperties.TheAllowsCacheControlHeaderFlag;
	}

	@Deprecated
	public void setResourceManager(final WOResourceManager aResourceManager) {
		_resourceManager = aResourceManager;
	}

	public WOResourceManager resourceManager() {
		if (_resourceManager == null) {
			_resourceManager = createResourceManager();
		}
		return _resourceManager;
	}

	public WOResponse dispatchRequest(final WORequest aRequest) {
		final NSNotificationCenter aDefaultCenter = NSNotificationCenter.defaultCenter();
		_TheLastApplicationAccessTime = System.currentTimeMillis();
		aDefaultCenter.postNotification("ApplicationWillDispatchRequestNotification", aRequest);
		final WORequestHandler aHandler = handlerForRequest(aRequest);
		WOResponse aResponse = aHandler.handleRequest(aRequest);
		if (aResponse == null) {
			debugString("<" + _WOAppClassName + "> !!! Response is null !!!");
			aResponse = application().createResponseInContext(null);
		}
		aDefaultCenter.postNotification("ApplicationDidDispatchRequestNotification", aResponse);
		aRequest._setContext((WOContext) null);
		return aResponse;
	}

	public void awake() {
	}

	public void takeValuesFromRequest(final WORequest aRequest, final WOContext aContext) {
		final WOSession aSession = aContext._session();
		if (aSession != null) {
			aSession.takeValuesFromRequest(aRequest, aContext);
		}
	}

	public WOActionResults invokeAction(final WORequest aRequest, final WOContext aContext) {
		final WOSession aSession = aContext._session();
		WOActionResults result = null;
		if (aSession != null) {
			result = aSession.invokeAction(aRequest, aContext);
		}
		if (aContext.shouldNotStorePageInBacktrackCache()) {
			if (result == aContext.page()) {
				NSLog.out.appendln(
						"invokeAction: An non stored response return context.page(), which will result in suboptimal performance.");
			}
			if (result == null) {
				result = aContext.response();
			}
		}
		return result;
	}

	public void appendToResponse(final WOResponse aResponse, final WOContext aContext) {
		final WORequest aRequest = aContext.request();
		final WOSession theSession = aContext._session();
		if (aContext._refuseThisRequest()) {
			NSLog.err.appendln("<WOApplication> !!! appendToResponse: called with refuseNewSessions set !!!");
			synchronized (this) {
				if (NSLog.debugLoggingAllowedForLevelAndGroups(2, 4L)) {
					NSLog.debug.appendln("!!! Request will be REDIRECTED to non-refusing instance.");
				}
				aResponse.setStatus(301);
				aResponse.setHeader(_newLocationForRequest(aRequest), "Location");
				if (theSession != null) {
					theSession.terminate();
				}
			}
		}
		if (theSession != null) {
			theSession.appendToResponse(aResponse, aContext);
		}
		if (aRequest != null && aRequest.headerForKey("x-webobjects-recording") != null
				&& aRequest.headerForKey("x-webobjects-recording").length() > 0 || recordingPath() != null) {
			if (theSession != null) {
				final String sid = theSession.sessionID();
				if (sid != null) {
					aResponse.setHeader(sid, "x-webobjects-session-id");
				}
				if (theSession.storesIDsInURLs()) {
					aResponse.setHeader("yes", "x-webobjects-ids-url");
				}
				if (theSession.storesIDsInCookies()) {
					aResponse.setHeader("yes", "x-webobjects-ids-cookie");
				}
			}
			if (aRequest != null) {
				aResponse.setHeader(String.valueOf(aRequest.applicationNumber()), "x-webobjects-application-number");
			}
		}
	}

	public void sleep() {
	}

	public void setSessionStore(final WOSessionStore aSessionStore) {
		if (aSessionStore == null) {
			throw new IllegalArgumentException("<" + _WOAppClassName + ">: Session store reference must not be null");
		}
		_sessionStore = aSessionStore;
	}

	public WOSessionStore sessionStore() {
		return _sessionStore;
	}

	public void saveSessionForContext(final WOContext aContext) {
		final WOSession aSession = aContext._session();
		if (aSession != null) {
			try {
				aSession._sleepInContext(null);
				NSDelayedCallbackCenter.defaultCenter().eventEnded();
			} finally {
				_sessionStore.checkInSessionForContext(aContext);
			}
		}
		aContext._setSession(null);
	}

	public WOSession restoreSessionWithID(final String aSessionID, final WOContext aContext) {
		final WOSession aSession = _sessionStore.checkOutSessionWithID(aSessionID, aContext.request());
		if (aSession != null) {
			aContext._setSession(aSession);
			aSession._awakeInContext(aContext);
		}
		NSNotificationCenter.defaultCenter().postNotification("SessionDidRestoreNotification", aSession);
		return aSession;
	}

	protected Class<?> _sessionClass() {
		if (_theSessionClass == null) {
			Class<WOSession> sessionClass = null;
			sessionClass = _NSUtilities.classWithName(
					(_WOAppPackageName == null || _WOAppPackageName.length() == 0 ? "" : _WOAppPackageName + ".")
							+ "Session");
			if (sessionClass == null) {
				sessionClass = WOSession.class;
			}
			if (!WOSession.class.isAssignableFrom(sessionClass)) {
				throw new IllegalArgumentException("<" + getClass().getName() + "> Class 'Session' exists ("
						+ sessionClass + ") but is not a subclass of WOSession.");
			}
			_theSessionClass = sessionClass;
		}
		return _theSessionClass;
	}

	public WOSession createSessionForRequest(final WORequest aRequest) {
		final Class<?> aSessionClass = _sessionClass();
		return (WOSession) _NSUtilities.instantiateObject(aSessionClass, null, null, true, isDebuggingEnabled());
	}

	public boolean shouldRestoreSessionOnCleanEntry(final WORequest aRequest) {
		return false;
	}

	public void setContextClassName(final String name) {
		if (name != null) {
			WOProperties.TheContextClassName = name;
		}
	}

	public String contextClassName() {
		if (WOProperties.TheContextClassName == null) {
			final String contextClassName = NSProperties.getProperty(WOProperties._ContextClassNameKey);
			setContextClassName(contextClassName);
		}
		return WOProperties.TheContextClassName;
	}

	public WOContext createContextForRequest(final WORequest aRequest) {
		WOContext aContext = null;
		final Class<?> contextClass = _NSUtilities.classWithName(contextClassName());
		if (contextClass != null) {
			final Object[] arguments = { aRequest };
			aContext = (WOContext) _NSUtilities.instantiateObject(contextClass, _createContextForRequestParams,
					arguments, true, isDebuggingEnabled());
		}
		if (aContext == null) {
			throw new InstantiationError("<" + _WOAppClassName + ">: Unable to create " + contextClassName());
		}
		return aContext;
	}

	WOApplication(final String sessionStoreClassName) {
		_createContextForRequestParams = new Class<?>[] { WORequest.class };
		_adaptorName = "";
		_adaptorPath = "";
		_documentRoot = "";
		_globalLock = new Object();
		_initWOApp(false);
		_setApplication(this);
		NSLog.debug.appendln("WebObjects version = " + getWebObjectsVersion());
		_name = NSBundle.mainBundle() != null ? NSBundle.mainBundle().name() : "WOA";
		_currentRunLoop = null;
		_allowsConcurrentRequestHandling = false;
		_minimumActiveSessions = 0;
		_activeSessionsCount = 0;
		_refusingNewClients = false;
		_componentDefinitionCache = new _NSThreadsafeMutableDictionary<>(new NSMutableDictionary<>(128));
		_dynamicLoadingEnabled = true;
		_pageCacheSize = 0;
		_pageFragmentCacheSize = _pageCacheSize;
		_permanentCacheSize = _pageCacheSize;
		_pageRefreshOnBacktrackEnabled = false;
		if (NSBundle.mainBundle() != null) {
			_inRapidTurnaroundMode = _initializeRapidTurnaroundMode();
		} else {
			_inRapidTurnaroundMode = false;
		}
		setStatisticsStore(null);
		setSessionStoreClassName(sessionStoreClassName);
		_defaultRequestHandler = null;
		_requestHandlers = new _NSThreadsafeMutableDictionary<>(new NSMutableDictionary<>());
		final WOComponentRequestHandler componentRequestHandler = new WOComponentRequestHandler();
		registerRequestHandler(componentRequestHandler, componentRequestHandlerKey());
		registerRequestHandler(new WODirectActionRequestHandler(), directActionRequestHandlerKey());
		setDefaultRequestHandler(componentRequestHandler);
		_lifebeatThread = null;
		_timer = null;
		_terminating = false;
		_recorder = null;
	}

	public WOApplication() {
		_createContextForRequestParams = new Class<?>[] { WORequest.class };
		_adaptorName = "";
		_adaptorPath = "";
		_documentRoot = "";
		_globalLock = new Object();
		try {
			_initWOApp(true);
			_setApplication(this);
			NSLog.debug.appendln("WebObjects version = " + getWebObjectsVersion());
			final String appName = NSProperties.getProperty(WOProperties._ApplicationNameKey);
			if (appName == null) {
				_name = NSBundle.mainBundle().name();
			} else {
				_name = appName;
			}
			_currentRunLoop = _WORunLoop.currentRunLoop();
			if (!wasMainInvoked()) {
				setAdaptor("WONullAdaptor");
				_setLifebeatEnabled(false);
			}
			_initAdaptors();
			if (_hostAddress == null) {
				try {
					_hostAddress = InetAddress.getLocalHost();
				} catch (final UnknownHostException exception) {
					NSLog.err.appendln("Failed to get localhost address");
					throw NSForwardException._runtimeExceptionForThrowable(exception);
				}
				_setHost(_hostAddress.getHostName());
			}
			_allowsConcurrentRequestHandling = allowsConcurrentRequestHandling();
			if (port().intValue() != -1) {
				EOTemporaryGlobalID._setProcessIdentificationBytesFromInt(port().intValue());
			}
			EOTemporaryGlobalID._setHostIdentificationBytes(hostAddress().getAddress());
			_minimumActiveSessions = 0;
			_activeSessionsCount = 0;
			_refusingNewClients = false;
			_componentDefinitionCache = new _NSThreadsafeMutableDictionary<>(new NSMutableDictionary<>(128));
			_dynamicLoadingEnabled = true;
			_pageCacheSize = 30;
			_pageFragmentCacheSize = _pageCacheSize;
			_permanentCacheSize = _pageCacheSize;
			_pageRefreshOnBacktrackEnabled = true;
			_resourceManager = createResourceManager();
			_inRapidTurnaroundMode = _initializeRapidTurnaroundMode();
			setStatisticsStore(new WOStatisticsStore());
			final String sessionStoreClassName = NSProperties.getProperty(WOProperties._SessionStoreClassNameKey);
			setSessionStoreClassName(sessionStoreClassName);
			_defaultRequestHandler = null;
			_requestHandlers = new _NSThreadsafeMutableDictionary<>(new NSMutableDictionary<>(16));
			_registerRequestHandlers();
			if (!"wotaskd".equals(name()) && lifebeatEnabled()) {
				final String lifebeatIntervalString = NSProperties.getProperty(WOProperties._LifebeatIntervalKey);
				long lifebeatIntervalLong = 0L;
				try {
					lifebeatIntervalLong = Long.parseLong(lifebeatIntervalString);
				} catch (final NumberFormatException e) {
					NSLog.err.appendln(
							"<WOApplication> WOLifebeatInterval " + lifebeatIntervalString + " specified incorrectly");
					NSLog._conditionallyLogPrivateException(e);
				}
				if (lifebeatIntervalLong < 1L) {
					lifebeatIntervalLong = 30L;
				}
				lifebeatIntervalLong *= 1000L;
				_lifebeatThread = new _LifebeatThread(name(), port().intValue(), hostAddress(),
						lifebeatDestinationPort(), lifebeatIntervalLong);
				_lifebeatThread.setDaemon(true);
				_lifebeatThread.start();
			} else {
				_lifebeatThread = null;
			}
			_TheLastApplicationAccessTime = System.currentTimeMillis();
			_timeOut = NSTimestamp.DistantFuture.getTime() - _TheLastApplicationAccessTime;
			_timer = null;
			_terminating = false;
			if (recordingPath() != null) {
				String recordingClassName = NSProperties.getProperty(_WORecordingClassNameKey);
				if (recordingClassName == null) {
					recordingClassName = _WORecordingClassName;
				}
				_recorder = _instanceOfNamedClassAssignableFrom(recordingClassName, WORecording.class,
						WORecording.class);
			} else {
				_recorder = null;
			}
		} catch (final Exception e) {
			if (NSLog.debugLoggingAllowedForLevelAndGroups(1, 4L)) {
				NSLog.err.appendln("<WOApplication>: Cannot be initialized." + e.getMessage());
			}
			throw new NSForwardException(e, "<WOApplication>: Cannot be initialized.");
		}
	}

	public WOResponse createResponseInContext(final WOContext aContext) {
		return new WOResponse();
	}

	public WOResourceManager createResourceManager() {
		return new WOResourceManager();
	}

	public WORequest createRequest(final String aMethod, final String aURL, final String anHTTPVersion,
			final Map<String, ? extends List<String>> someHeaders, final NSData aContent,
			final Map<String, Object> someInfo) {
		return new WORequest(aMethod, aURL, anHTTPVersion, someHeaders, aContent, someInfo);
	}

	public WOSession _initializeSessionInContext(final WOContext aContext) {
		if (isRefusingNewSessions()) {
			NSLog.err
					.appendln("<WOApplication> !!! _initializeSessionInContext: called with refuseNewSessions set !!!");
			aContext._set_refuseThisRequest(true);
		}
		synchronized (this) {
			_activeSessionsCount++;
		}
		final WOSession sessionInstance = createSessionForRequest(aContext.request());
		if (sessionInstance == null) {
			synchronized (this) {
				_activeSessionsCount--;
			}
			NSLog.debug.appendln("<" + _WOAppClassName + ": Unable to create new session.");
			return null;
		}
		aContext._setSession(sessionInstance);
		sessionInstance._awakeInContext(aContext);
		NSNotificationCenter.defaultCenter().postNotification("SessionDidCreateNotification", sessionInstance);
		return sessionInstance;
	}

	public int activeSessionsCount() {
		return _activeSessionsCount;
	}

	protected void _finishInitializingSession(final WOSession aSession) {
	}

	protected void _discountTerminatedSession() {
		int anActiveSessionCount;
		synchronized (this) {
			anActiveSessionCount = --_activeSessionsCount;
		}
		if (isRefusingNewSessions() && anActiveSessionCount < _minimumActiveSessions + 1) {
			NSLog.err
					.appendln("<" + _WOAppClassName + ">: refusing new clients and below min active session threshold");
			NSLog.err.appendln("<" + _WOAppClassName + ">: about to terminate...");
			terminate();
		}
	}

	public void setPageCacheSize(final int anUnsigned) {
		_pageCacheSize = anUnsigned < 0 ? 0 : anUnsigned;
	}

	public int pageCacheSize() {
		return _pageCacheSize;
	}

	@Override
	public int getPageCacheSize() {
		return pageCacheSize();
	}

	public void setPageFragmentCacheSize(final int anUnsigned) {
		_pageFragmentCacheSize = anUnsigned < 0 ? 0 : anUnsigned;
	}

	public int pageFragmentCacheSize() {
		return _pageFragmentCacheSize;
	}

	public int getPageFragmentCacheSize() {
		return pageFragmentCacheSize();
	}

	public void setPermanentPageCacheSize(final int anUnsigned) {
		_permanentCacheSize = anUnsigned < 0 ? 0 : anUnsigned;
	}

	public int permanentPageCacheSize() {
		return _permanentCacheSize;
	}

	public void setPageRefreshOnBacktrackEnabled(final boolean aFlag) {
		_pageRefreshOnBacktrackEnabled = aFlag;
	}

	public boolean isPageRefreshOnBacktrackEnabled() {
		return _pageRefreshOnBacktrackEnabled;
	}

	@Override
	public boolean getIsPageRefreshOnBacktrackEnabled() {
		return isPageRefreshOnBacktrackEnabled();
	}

	public WOComponent pageWithName(final String aName, final WOContext aContext) {
		WOComponentDefinition componentDefinition;
		WOComponent pageInstance = null;
		final String pageName = aName != null ? aName : "Main";
		if (aContext == null) {
			throw new IllegalArgumentException(
					"<" + _WOAppClassName + ">: Unable to create page '" + pageName + "'.  No context was passed.");
		}
		synchronized (this) {
			componentDefinition = _componentDefinition(pageName, aContext._languages());
		}
		if (componentDefinition != null) {
			WOEvent anEvent = null;
			if (_IsEventLoggingEnabled) {
				anEvent = (WOEvent) EOEventCenter.newEventOfClass(Event.class, pageWithNameEvent);
				EOEventCenter.markStartOfEvent(anEvent, pageName);
				anEvent.setComponentName(pageName);
				anEvent.setPageName(pageName);
			}
			pageInstance = componentDefinition.componentInstanceInContext(aContext);
			if (anEvent != null) {
				if (pageInstance.isEventLoggingEnabled()) {
					EOEventCenter.markEndOfEvent(anEvent);
				} else {
					EOEventCenter.cancelEvent(anEvent);
				}
			}
			pageInstance._awakeInContext(aContext);
			pageInstance._setIsPage(true);
		}
		if (pageInstance == null) {
			throw new WOPageNotFoundException("<" + _WOAppClassName + ">: Unable to create page '" + pageName + "'.");
		}
		return pageInstance;
	}

	public boolean _isPageRecreationEnabled() {
		return _pageCacheSize == 0;
	}

	public WOElement dynamicElementWithName(final String aName,
			final NSDictionary<String, WOAssociation> someAssociations,
			final WOElement anElement, final NSArray<String> aLanguageArray) {
		WOElement elementInstance = null;
		if (aName == null) {
			throw new IllegalArgumentException(
					"<" + _WOAppClassName + ">: No name provided for dynamic element creation.");
		}
		final Class<?> elementClass = _NSUtilities.classWithName(aName);
		if (elementClass != null && WODynamicElement.class.isAssignableFrom(elementClass)) {
			final Class<?>[] params = WODynamicElement._ConstructorParameters;
			final Object[] arguments = { aName, someAssociations, anElement };
			elementInstance = (WOElement) _NSUtilities.instantiateObject(elementClass, params, arguments, true,
					isDebuggingEnabled());
		}
		if (elementInstance == null) {
			final WOComponentDefinition componentDefinition = _componentDefinition(aName, aLanguageArray);
			if (componentDefinition != null) {
				elementInstance = componentDefinition.componentReferenceWithAssociations(someAssociations, anElement);
			}
		}
		return elementInstance;
	}

	private NSMutableArray<String> _initComponentBearingFrameworksFromBundleArray(
			final NSArray<NSBundle> aBundleArray) {
		NSBundle aBundle = null;
		final Enumeration<NSBundle> anEnumerator = aBundleArray.objectEnumerator();
		final NSMutableArray<String> bundlePathArray = new NSMutableArray<>(aBundleArray.count());
		while (anEnumerator.hasMoreElements()) {
			aBundle = anEnumerator.nextElement();
			if (WOBundle.hasWOComponents(aBundle)) {
				bundlePathArray.addObject(aBundle.name());
			}
		}
		return bundlePathArray;
	}

	private NSMutableArray<String> _componentBearingFrameworks() {
		final NSArray<NSBundle> aBundleArray = NSBundle.frameworkBundles();
		final int aBundleArrayCount = aBundleArray.count();
		if (TheLoadedFrameworkCount != aBundleArrayCount) {
			TheLoadedFrameworkCount = aBundleArrayCount;
			TheComponentBearingFrameworks = _initComponentBearingFrameworksFromBundleArray(aBundleArray);
		}
		return TheComponentBearingFrameworks;
	}

	protected URL combinedComponentPathURL(final WOResourceManager aResourceManager, final String templateName,
			final String aFrameworkName, final String aLanguage, final boolean refreshProjectOnCacheMiss) {
		final StringBuilder combinedPath = new StringBuilder(templateName.length() + 3);
		combinedPath.append(templateName);
		combinedPath.append(".");
		final String currPath = combinedPath.toString();
		String currName = "";
		URL aCombinedComponentPathURL = null;
		for (final String element : WOMLTemplateExtensions.instance().combinedTemplateFileExtensions()) {
			currName = currPath + element;
			aCombinedComponentPathURL = aResourceManager._pathURLForResourceNamed(currName, aFrameworkName, aLanguage,
					refreshProjectOnCacheMiss);
			if (aCombinedComponentPathURL != null) {
				return aCombinedComponentPathURL;
			}
		}
		return null;
	}

	private WOComponentDefinition _loadComponentDefinition(final String aComponentName, final String aLanguage,
			final boolean refreshProjectOnCacheMiss) {
		WOComponentDefinition aComponentDefinition = null;
		String aComponentBaseURL = null;
		String aFrameworkName = null;
		NSBundle classBundle = null;
		Class<?> componentClass = null;
		final int _dotIndex = aComponentName.lastIndexOf(".");
		final String templateName = _dotIndex != -1 ? aComponentName.substring(_dotIndex + 1) : aComponentName;
		final StringBuilder buffer = new StringBuilder(templateName.length() + 3);
		buffer.append(templateName);
		buffer.append('.');
		buffer.append(WOComponent._Extension);
		String aFullComponentName = _NSStringUtilities.stringFromBuffer(buffer);
		final WOResourceManager aResourceManager = resourceManager();
		URL aComponentPathURL = aResourceManager._pathURLForResourceNamed(aFullComponentName, null, aLanguage,
				refreshProjectOnCacheMiss);
		URL aCombinedComponentPathURL = null;
		if (aComponentPathURL == null) {
			aCombinedComponentPathURL = combinedComponentPathURL(aResourceManager, templateName, null, aLanguage,
					refreshProjectOnCacheMiss);
			if (aCombinedComponentPathURL != null) {
				aComponentPathURL = aCombinedComponentPathURL;
				aFullComponentName = aComponentPathURL.getFile();
			}
		}
		if (aComponentPathURL == null) {
			final NSMutableArray<String> aFrameworkNameArray = _componentBearingFrameworks();
			if (aFrameworkNameArray != null) {
				final Enumeration<String> aNameEnumerator = aFrameworkNameArray.objectEnumerator();
				while (aNameEnumerator.hasMoreElements()) {
					aFrameworkName = aNameEnumerator.nextElement();
					aComponentPathURL = aResourceManager._pathURLForResourceNamed(aFullComponentName, aFrameworkName,
							aLanguage, refreshProjectOnCacheMiss);
					if (aComponentPathURL != null) {
						break;
					}
					aComponentPathURL = combinedComponentPathURL(aResourceManager, templateName, aFrameworkName,
							aLanguage, refreshProjectOnCacheMiss);
					if (aComponentPathURL != null) {
						break;
					}
				}
			}
		}
		if (aComponentPathURL == null && aComponentName.indexOf(".") > 0) {
			final String nonQualifiedComponentNameWithExtension = aFullComponentName;
			try {
				componentClass = _NSUtilities._classWithFullySpecifiedName(aComponentName);
			} catch (final Exception e) {
			}
			if (componentClass != null) {
				classBundle = NSBundle.bundleForClass(componentClass);
				if (classBundle == null) {
					aFrameworkName = null;
				} else {
					aFrameworkName = classBundle.name();
					if (classBundle.isFramework()) {
						aComponentPathURL = aResourceManager._pathURLForResourceNamed(
								nonQualifiedComponentNameWithExtension, aFrameworkName, aLanguage,
								refreshProjectOnCacheMiss);
					} else {
						aComponentPathURL = aResourceManager._pathURLForResourceNamed(
								nonQualifiedComponentNameWithExtension, null, aLanguage, refreshProjectOnCacheMiss);
					}
				}
			}
		}
		if (aComponentPathURL != null || classBundle == null && componentClass != null) {
			NSMutableArray<String> aLanguageArray = null;
			if (aLanguage != null) {
				aLanguageArray = new NSMutableArray<>(aLanguage);
			}
			if (classBundle == null && componentClass != null) {
				aComponentBaseURL = applicationBaseURL() + "/" + componentClass.getName().replace('.', '/');
			} else {
				aComponentBaseURL = aResourceManager.urlForResourceNamed(aFullComponentName, aFrameworkName,
						aLanguageArray, null);
			}
			aComponentDefinition = new WOComponentDefinition(aComponentName, aComponentPathURL, aComponentBaseURL,
					aFrameworkName, aLanguage);
		}
		return aComponentDefinition;
	}

	private void _preloadAllLocalizedComponentDefinitions(final String aComponentName) {
		for (final Object object : resourceManager()._frameworkProjectBundles()) {
			((WODeployedBundle) object).relativePathForResource("Preloading all localizations", (String) null);
		}
		final NSArray<String> languages = _expectedLanguages();
		WOComponentDefinition aComponentDefinition = WOComponentDefinition.NotFound;
		WOComponentDefinition componentDefinitionWithNoWrapper = null;
		final StringBuffer componentKeyBuffer = new StringBuffer(aComponentName);
		componentKeyBuffer.append('.');
		String componentKey = null;
		final int length = componentKeyBuffer.length();
		boolean isLocalized = false;
		Enumeration<String> aLanguageEnumerator;
		for (aLanguageEnumerator = languages.objectEnumerator(); aLanguageEnumerator.hasMoreElements();) {
			final String aLanguage = aLanguageEnumerator.nextElement();
			componentKeyBuffer.setLength(length);
			componentKeyBuffer.append(aLanguage);
			componentKey = _NSStringUtilities.stringFromBuffer(componentKeyBuffer);
			aComponentDefinition = _loadComponentDefinition(aComponentName, aLanguage, true);
			if (aComponentDefinition == null) {
				aComponentDefinition = WOComponentDefinition.NotFound;
			} else {
				isLocalized = true;
			}
			_componentDefinitionCache.setObjectForKey(aComponentDefinition, componentKey);
		}
		WOComponentDefinition defaultComponentDefinition = _componentDefinitionCache.objectForKey(aComponentName);
		if (defaultComponentDefinition == null) {
			defaultComponentDefinition = _loadComponentDefinition(aComponentName, (String) null, true);
			if (defaultComponentDefinition == null) {
				defaultComponentDefinition = WOComponentDefinition.NotFound;
			}
			_componentDefinitionCache.setObjectForKey(defaultComponentDefinition, aComponentName);
		}
		if (!isLocalized) {
			for (aLanguageEnumerator = languages.objectEnumerator(); aLanguageEnumerator.hasMoreElements();) {
				final String aLanguage = aLanguageEnumerator.nextElement();
				componentKeyBuffer.setLength(length);
				componentKeyBuffer.append(aLanguage);
				componentKey = _NSStringUtilities.stringFromBuffer(componentKeyBuffer);
				if (defaultComponentDefinition != WOComponentDefinition.NotFound) {
					_componentDefinitionCache.setObjectForKey(defaultComponentDefinition, componentKey);
					continue;
				}
				if (componentDefinitionWithNoWrapper == null) {
					componentDefinitionWithNoWrapper = _componentDefinitionFromClassNamed(aComponentName);
				}
				if (componentDefinitionWithNoWrapper == null) {
					componentDefinitionWithNoWrapper = WOComponentDefinition.NotFound;
				}
				_componentDefinitionCache.setObjectForKey(componentDefinitionWithNoWrapper, aComponentName);
			}
		}
	}

	private WOComponentDefinition _componentDefinition(final String componentKey, final String aComponentName,
			final String aLanguage,
			final boolean refreshProjectOnCacheMiss) {
		WOComponentDefinition aComponentDefinition = _componentDefinitionCache.objectForKey(componentKey);
		if (aComponentDefinition == null) {
			if (isCachingEnabled() && !_inRapidTurnaroundMode && aLanguage != null) {
				_preloadAllLocalizedComponentDefinitions(aComponentName);
				aComponentDefinition = _componentDefinitionCache.objectForKey(componentKey);
				if (aComponentDefinition == null) {
					_componentDefinitionCache.setObjectForKey(WOComponentDefinition.NotFound, componentKey);
				}
			} else {
				aComponentDefinition = _loadComponentDefinition(aComponentName, aLanguage, refreshProjectOnCacheMiss);
				if (aComponentDefinition == null) {
					if (refreshProjectOnCacheMiss) {
						_componentDefinitionCache.setObjectForKey(WOComponentDefinition.NotFound, componentKey);
					}
				} else {
					_componentDefinitionCache.setObjectForKey(aComponentDefinition, componentKey);
				}
			}
		}
		return aComponentDefinition;
	}

	public boolean _rapidTurnaroundActiveForAnyProject() {
		return _inRapidTurnaroundMode;
	}

	public void _removeComponentDefinitionCacheContents() {
		_componentDefinitionCache.removeAllObjects();
	}

	public void _addToExpectedLanguages(final NSArray<String> someLanguages) {
		_expectedLanguages.addObjectsFromArray(someLanguages);
	}

	public NSArray<String> _expectedLanguages() {
		return _expectedLanguages.allObjects();
	}

	public WOComponentDefinition _componentDefinitionFromClassNamed(final String aComponentName) {
		WOComponentDefinition aComponentDefinition = _componentDefinitionCache.objectForKey(aComponentName);
		if (aComponentDefinition == null || aComponentDefinition == WOComponentDefinition.NotFound
				&& NSPathUtilities.lastPathComponent(aComponentName).equals(aComponentName)) {
			Class<WOComponent> aClass = null;
			if (aComponentDefinition == WOComponentDefinition.NotFound) {
				aClass = WOBundle.lookForClassInAllBundles(aComponentName);
			} else {
				aClass = _NSUtilities.classWithName(aComponentName);
			}
			if (aClass != null && aClass != WOComponent.class && WOComponent.class.isAssignableFrom(aClass)) {
				final NSBundle aFrameworkBundle = NSBundle.bundleForClass(aClass);
				URL aComponentPathURL = null;
				String aFrameworkName = null;
				if (aFrameworkBundle != null) {
					try {
						aComponentPathURL = new URL(aFrameworkBundle._bundleURLPrefix());
					} catch (final Exception e) {
					}
					aFrameworkName = aFrameworkBundle.name();
				}
				final String aComponentBaseURL = "/ERROR/RelativeUrlsNotSupportedWhenComponentHasNoWrapper";
				aComponentDefinition = new WOComponentDefinition(aComponentName, aComponentPathURL, aComponentBaseURL,
						aFrameworkName, null);
			}
			if (aComponentDefinition == null) {
				aComponentDefinition = WOComponentDefinition.NotFound;
			}
			_componentDefinitionCache.setObjectForKey(aComponentDefinition, aComponentName);
			if (aClass != null) {
				_componentDefinitionCache.setObjectForKey(aComponentDefinition, aClass.getName());
			}
		}
		if (aComponentDefinition == WOComponentDefinition.NotFound) {
			aComponentDefinition = null;
		}
		return aComponentDefinition;
	}

	public WOComponentDefinition _componentDefinition(final String aComponentName,
			final NSArray<String> aLanguageArray) {
		WOComponentDefinition aComponentDefinition = WOComponentDefinition.NotFound;
		NSArray<String> languageArray = null;
		if (aLanguageArray != null) {
			languageArray = aLanguageArray;
		} else {
			languageArray = NSArray.emptyArray();
		}
		final int alaCount = languageArray.count();
		final StringBuilder componentKeyBuffer = new StringBuilder(aComponentName);
		componentKeyBuffer.append('.');
		final int length = componentKeyBuffer.length();
		int i;
		for (i = 0; i < alaCount; i++) {
			final String aLanguage = languageArray.objectAtIndex(i);
			componentKeyBuffer.setLength(length);
			componentKeyBuffer.append(aLanguage);
			final String componentKey = componentKeyBuffer.toString();
			aComponentDefinition = _componentDefinition(componentKey, aComponentName, aLanguage, false);
			if (aComponentDefinition != WOComponentDefinition.NotFound && aComponentDefinition != null) {
				break;
			}
		}
		if (aComponentDefinition == WOComponentDefinition.NotFound || aComponentDefinition == null) {
			aComponentDefinition = _componentDefinition(aComponentName, aComponentName, null, false);
		}
		if ((aComponentDefinition == WOComponentDefinition.NotFound || aComponentDefinition == null)
				&& WOProjectBundle.refreshProjectBundlesOnCacheMiss()) {
			for (i = 0; i < alaCount; i++) {
				final String aLanguage = languageArray.objectAtIndex(i);
				componentKeyBuffer.setLength(length);
				componentKeyBuffer.append(aLanguage);
				final String componentKey = componentKeyBuffer.toString();
				aComponentDefinition = _componentDefinition(componentKey, aComponentName, aLanguage, true);
				if (aComponentDefinition != WOComponentDefinition.NotFound && aComponentDefinition != null) {
					break;
				}
			}
			if (aComponentDefinition == WOComponentDefinition.NotFound || aComponentDefinition == null) {
				aComponentDefinition = _componentDefinition(aComponentName, aComponentName, null, true);
			}
		}
		if (aComponentDefinition == WOComponentDefinition.NotFound || aComponentDefinition == null) {
			aComponentDefinition = _componentDefinitionFromClassNamed(aComponentName);
		}
		return aComponentDefinition;
	}

	private boolean _isDynamicLoadingEnabled() {
		return _dynamicLoadingEnabled;
	}

	@Deprecated
	public void setPrintsHTMLParserDiagnostics(final boolean aBOOL) {
	}

	@Deprecated
	public boolean printsHTMLParserDiagnostics() {
		return true;
	}

	public void setStatisticsStore(final WOStatisticsStore aStatisticsStore) {
		_statisticsStore = aStatisticsStore;
	}

	public WOStatisticsStore statisticsStore() {
		return _statisticsStore;
	}

	public NSDictionary statistics() {
		final WOStatisticsStore store = _statisticsStore;
		return store != null ? store.statistics() : null;
	}

	public synchronized void refuseNewSessions(final boolean aVal) {
		if (aVal && isDirectConnectEnabled()) {
			throw new IllegalStateException(
					"Cannot refuse new sessions when in development mode (direct connect enabled)");
		}
		_refusingNewClients = aVal;
		if (_refusingNewClients && activeSessionsCount() <= minimumActiveSessionsCount()) {
			NSLog.debug
					.appendln("<" + _WOAppClassName + ">: refusing new clients and below min active session threshold");
			NSLog.debug.appendln("<" + _WOAppClassName + ">: about to terminate...");
			terminate();
		}
	}

	public boolean isRefusingNewSessions() {
		return _refusingNewClients;
	}

	@Override
	public boolean getIsRefusingNewSessions() {
		return isRefusingNewSessions();
	}

	public void setMinimumActiveSessionsCount(final int aVal) {
		_minimumActiveSessions = aVal;
	}

	public int minimumActiveSessionsCount() {
		return _minimumActiveSessions;
	}

	@Override
	public int getMinimumActiveSessionsCount() {
		return minimumActiveSessionsCount();
	}

	private WOResponse _invokeDefaultException(final String aPageName, final Exception anException) {
		WOResponse aResponse = null;
		final Class<?>[] parameters = { Exception.class };
		final Object[] arguments = { anException };
		arguments[0] = anException;
		Method anErrorMethod = null;
		try {
			anErrorMethod = WODefaultExceptions.class.getMethod("default" + aPageName, parameters);
		} catch (final Exception exception) {
			NSLog.err.appendln(
					"<" + _WOAppClassName + "> Internal Error while handling Exception :" + exception.toString());
			if (NSLog.debugLoggingAllowedForLevelAndGroups(1, 4L)) {
				NSLog.debug.appendln(exception);
			}
		}
		try {
			if (anErrorMethod != null) {
				aResponse = (WOResponse) anErrorMethod.invoke(null, arguments);
			}
		} catch (final Exception e1) {
			NSLog.err.appendln("<" + _WOAppClassName + "> Internal Error while handling Exception :" + e1.toString());
			if (NSLog.debugLoggingAllowedForLevelAndGroups(1, 4L)) {
				NSLog.debug.appendln(e1);
				NSLog.debug.appendln("Original Exception:\n");
				NSLog.debug.appendln(anException);
			}
		}
		return aResponse;
	}

	private WOResponse _handleError(final String aPageName, final Exception anException, final WOContext aContext) {
		WOResponse aResponse = null;
		try {
			final WOComponent anErrorPage = pageWithName(aPageName, aContext);
			if (anException != null) {
				anErrorPage.takeValueForKey(anException, "exception");
			}
			aResponse = anErrorPage.generateResponse();
		} catch (final WOPageNotFoundException e) {
			aResponse = _invokeDefaultException(aPageName, anException);
		} catch (final Throwable e) {
			String eString = null;
			String anExceptionString = null;
			if (NSLog.debugLoggingAllowedForLevelAndGroups(1, 4L)) {
				final ByteArrayOutputStream byteStream = new ByteArrayOutputStream(500);
				final PrintStream printStream = new PrintStream(byteStream);
				e.printStackTrace(printStream);
				printStream.flush();
				eString = byteStream.toString();
				byteStream.reset();
				anException.printStackTrace(printStream);
				printStream.flush();
				anExceptionString = byteStream.toString();
			} else {
				eString = e.toString();
				anExceptionString = anException.toString();
			}
			final Exception e1 = new IllegalStateException("An Exception occurred while generating the Exception page '"
					+ aPageName + "'. This is most likely due to an error in '" + aPageName
					+ "' itself or WebObjects encountered an uncaught exception while creating a Session object.\n\n\nBelow are the logs of the original Exception which occured in "
					+ getClass().getName() + ", then the later Exception in " + aPageName + ".\n\nOriginal Exception:\n"
					+ anExceptionString + "\n\n" + aPageName + " Exception:\n" + eString);
			if (NSLog.debugLoggingAllowedForLevelAndGroups(1, 4L)) {
				NSLog.debug.appendln("<WOApplication> Exception occurred in " + aPageName + ": " + e.toString());
				NSLog.debug.appendln(anException);
			}
			aResponse = _invokeDefaultException(aPageName, e1);
		}
		return aResponse;
	}

	public WOResponse handleSessionCreationErrorInContext(final WOContext aContext) {
		return _handleError("WOSessionCreationError", null, aContext);
	}

	public WOResponse handleSessionRestorationErrorInContext(final WOContext aContext) {
		return _handleError("WOSessionRestorationError", null, aContext);
	}

	public WOResponse handlePageRestorationErrorInContext(final WOContext aContext) {
		return _handleError("WOPageRestorationError", null, aContext);
	}

	public WOResponse handleActionRequestError(final WORequest aRequest, final Exception exception, final String reason,
			final WORequestHandler aHandler, final String actionClassName, final String actionName,
			final Class<?> actionClass,
			final WOAction actionInstance) {
		return null;
	}

	public WOResponse handleException(final Exception anException, final WOContext aContext) {
		Exception exception = anException;
		final Throwable t = NSForwardException._originalThrowable(exception);
		if (t instanceof Exception) {
			exception = (Exception) t;
		}
		return _handleError("WOExceptionPage", exception, aContext);
	}

	public NSMutableDictionary<String, NSMutableArray<String>> handleMalformedCookieString(
			final RuntimeException anException,
			final String cookieString, final NSMutableDictionary<String, NSMutableArray<String>> aReturnDict) {
		final boolean _tolerateMalformedCookies = NSPropertyListSerialization
				.booleanForString(NSProperties.getProperty(WOProperties._TolerateMalformedCookiesKey));
		if (!_tolerateMalformedCookies) {
			throw anException;
		}
		NSLog.err.appendln("CookieParser: Error " + anException);
		NSLog.err.appendln("CookieParser: while parsing cookie header: " + cookieString);
		NSLog.err.appendln("CookieParser: Returning the cookies parsed before error.");
		return aReturnDict;
	}

	public void registerRequestHandler(final WORequestHandler aHandler, final String aRequestHandlerKey) {
		_requestHandlers.setObjectForKey(aHandler, aRequestHandlerKey);
	}

	public WORequestHandler removeRequestHandlerForKey(final String aRequestHandlerKey) {
		return (WORequestHandler) _requestHandlers.removeObjectForKey(aRequestHandlerKey);
	}

	public WORequestHandler defaultRequestHandler() {
		return _defaultRequestHandler;
	}

	public void setDefaultRequestHandler(final WORequestHandler aRequestHandler) {
		_defaultRequestHandler = aRequestHandler;
	}

	public WORequestHandler requestHandlerForKey(final String aKey) {
		WORequestHandler aRequestHandler = null;
		if (aKey != null) {
			aRequestHandler = _requestHandlers.objectForKey(aKey);
		}
		return aRequestHandler;
	}

	public NSArray<String> registeredRequestHandlerKeys() {
		return _requestHandlers.allKeys();
	}

	private WORequestHandler _staticResourceRequestHandler() {
		return requestHandlerForKey("_wr_");
	}

	public WORequestHandler handlerForRequest(final WORequest aRequest) {
		final String aRequestHandlerKey = aRequest.requestHandlerKey();
		WORequestHandler aRequestHandler = requestHandlerForKey(aRequestHandlerKey);
		if (aRequestHandler == null) {
			final WORequestHandler staticResourceRequestHandler = _staticResourceRequestHandler();
			if (staticResourceRequestHandler != null) {
				final String uri = aRequest.uri();
				final String extension = NSPathUtilities.pathExtension(uri);
				if (extension != null && extension.length() > 1) {
					final String fileType = extension.toLowerCase();
					final WOResourceManager resourceManager = resourceManager();
					final NSDictionary contentTypesDictionary = resourceManager._contentTypesDictionary();
					if (contentTypesDictionary.objectForKey(fileType) != null) {
						aRequestHandler = staticResourceRequestHandler;
					}
				}
			}
		}
		if (aRequestHandler == null) {
			aRequestHandler = defaultRequestHandler();
		}
		return aRequestHandler;
	}

	private void _registerRequestHandlers() {
		final WORequestHandler aComponentRequestHandler = new WOComponentRequestHandler();
		final WORequestHandler aResourceRequestHandler = new WOResourceRequestHandler();
		final WORequestHandler anActionHandler = new WODirectActionRequestHandler();
		final WORequestHandler anAdminHandler = new WODirectActionRequestHandler("WOAdminAction", "ping", false);
		final WODirectActionRequestHandler aStreamHandler = new WODirectActionRequestHandler();
		aStreamHandler.setAllowsContentInputStream(true);
		final WORequestHandler anAjaxHandler = new WOAjaxRequestHandler();
		final String aComponentRequestKey = componentRequestHandlerKey();
		final String aResourceKey = resourceRequestHandlerKey();
		final String anActionKey = directActionRequestHandlerKey();
		final String anAdminKey = "womp";
		final String aStreamKey = streamActionRequestHandlerKey();
		final String anAjaxKey = ajaxRequestHandlerKey();
		WORequestHandler defaultRequestHandler = null;
		registerRequestHandler(aComponentRequestHandler, aComponentRequestKey);
		registerRequestHandler(aResourceRequestHandler, aResourceKey);
		registerRequestHandler(anActionHandler, anActionKey);
		registerRequestHandler(anAdminHandler, anAdminKey);
		registerRequestHandler(aStreamHandler, aStreamKey);
		registerRequestHandler(anAjaxHandler, anAjaxKey);
		if (isDirectConnectEnabled()) {
			final WOStaticResourceRequestHandler staticResourceRequestHandler = new WOStaticResourceRequestHandler();
			registerRequestHandler(staticResourceRequestHandler, "_wr_");
		}
		final String defaultRequestHandlerString = defaultRequestHandlerClassName();
		try {
			final Class<WORequestHandler> defaultRequestHandlerClass = _NSUtilities
					.classWithName(defaultRequestHandlerString);
			if (defaultRequestHandlerClass != null) {
				defaultRequestHandler = defaultRequestHandlerClass.newInstance();
			}
		} catch (final Exception e) {
			debugString("<WOApplication> Exception during creation of defaultRequestHandler : " + e.toString());
		}
		if (defaultRequestHandler != null) {
			setDefaultRequestHandler(defaultRequestHandler);
		} else {
			setDefaultRequestHandler(aComponentRequestHandler);
		}
	}

	public String defaultRequestHandlerClassName() {
		return "com.webobjects.appserver._private.WOComponentRequestHandler";
	}

	@Deprecated
	public void debugString(final String aString) {
		_debugString(aString);
	}

	@Deprecated
	public static void _debugString(final String aString) {
		if (_isDebuggingEnabled()) {
			NSLog.debug.appendln(aString);
		}
	}

	@Deprecated
	public void logString(final String aString) {
		NSLog.err.appendln(aString);
	}

	public void logTakeValueForDeclarationNamed(final String aDeclarationName, final String aDeclarationType,
			final String aBindingName,
			final String anAssociationDescription, final Object aValue) {
		Object value = aValue;
		if (value instanceof String) {
			final StringBuilder buffer = new StringBuilder(((String) value).length() + 2);
			buffer.append('"');
			buffer.append(value);
			buffer.append('"');
			value = buffer;
		}
		NSLog.debug.appendln("[" + aDeclarationName + ":" + aDeclarationType + "] " + aBindingName + " <== ("
				+ anAssociationDescription + ": " + (value == null ? "null" : value.toString()) + ")");
	}

	public void logSetValueForDeclarationNamed(final String aDeclarationName, final String aDeclarationType,
			final String aBindingName,
			final String anAssociationDescription, final Object aValue) {
		Object value = aValue;
		if (value instanceof String) {
			final StringBuilder buffer = new StringBuilder(((String) value).length() + 2);
			buffer.append('"');
			buffer.append(value);
			buffer.append('"');
			value = buffer;
		}
		NSLog.debug.appendln("[" + aDeclarationName + ":" + aDeclarationType + "] (" + aBindingName + ": "
				+ (value == null ? "null" : value.toString()) + ") ==> " + anAssociationDescription);
	}

	@Deprecated
	public boolean monitoringEnabled() {
		return isMonitorEnabled();
	}

	@Deprecated
	public void terminateAfterTimeInterval(final double aTimeInterval) {
		setTimeOut(aTimeInterval);
	}

	protected void _terminateFromMonitor() {
		terminate();
	}

	public String _newLocationForRequest(final WORequest aRequest) {
		String newURI = null;
		if (aRequest != null) {
			final StringBuilder buffer = new StringBuilder(64);
			buffer.append(aRequest.adaptorPrefix());
			buffer.append('/');
			buffer.append(aRequest.applicationName());
			newURI = buffer.toString();
		}
		return newURI;
	}

	protected void _openInitialURL() {
		_launchServices._openInitialURL();
	}

	public String directConnectURL() {
		String anURLString = null;
		final String anAdaptorURL = cgiAdaptorURL();
		if (anAdaptorURL != null) {
			final int port = adaptors().count() != 0 ? adaptors().objectAtIndex(0).port() : 0;
			if (port != 0) {
				final int length = anAdaptorURL.length();
				final int firstSlashes = anAdaptorURL.indexOf(__hostDelimiter);
				if (firstSlashes != -1 && length - firstSlashes > 2) {
					final StringBuffer buffer = new StringBuffer(64);
					final int thirdSlash = anAdaptorURL.indexOf('/', firstSlashes + 2);
					final int endOfHostName = anAdaptorURL.indexOf(':', firstSlashes + 2);
					if (endOfHostName != -1) {
						buffer.append(anAdaptorURL.substring(0, endOfHostName));
						buffer.append(':');
						buffer.append(port);
						if (thirdSlash != -1) {
							buffer.append(anAdaptorURL.substring(thirdSlash));
						}
					} else if (thirdSlash != -1 && length - thirdSlash > 1) {
						buffer.append(anAdaptorURL.substring(0, thirdSlash));
						buffer.append(':');
						buffer.append(port);
						buffer.append(anAdaptorURL.substring(thirdSlash));
					} else {
						buffer.append(anAdaptorURL);
						buffer.append(':');
						buffer.append(port);
					}
					buffer.append('/');
					buffer.append(name());
					buffer.append(".woa");
					anURLString = new String(buffer);
				}
			}
		} else {
			debugString("No user default provided for key \"" + WOProperties._CGIAdaptorURLKey + "\"");
		}
		return anURLString;
	}

	@Override
	public String getDirectConnectURL() {
		return directConnectURL();
	}

	public String webserverConnectURL() {
		String anURLString = null;
		final String anAdaptorURL = cgiAdaptorURL();
		if (anAdaptorURL != null) {
			final StringBuffer buffer = new StringBuffer(128);
			buffer.append(anAdaptorURL);
			buffer.append('/');
			buffer.append(name());
			buffer.append(".woa/-");
			buffer.append(adaptors().count() != 0 ? adaptors().objectAtIndex(0).port() : 1);
			anURLString = new String(buffer);
		} else {
			debugString("No user default provided for key \"" + WOProperties._CGIAdaptorURLKey + "\"");
		}
		return anURLString;
	}

	@Override
	public String getWebserverConnectURL() {
		return webserverConnectURL();
	}

	public String servletConnectURL() {
		String anURLString = null;
		final String anAdaptorURL = cgiAdaptorURL();
		if (anAdaptorURL != null) {
			final StringBuffer buffer = new StringBuffer(128);
			buffer.append(anAdaptorURL);
			buffer.append('/');
			buffer.append(name());
			buffer.append(".woa");
			anURLString = new String(buffer);
		} else {
			debugString("No user default provided for key \"" + WOProperties._CGIAdaptorURLKey + "\"");
		}
		return anURLString;
	}

	@Override
	public String getServletConnectURL() {
		return servletConnectURL();
	}

	public final boolean adaptorsDispatchRequestsConcurrently() {
		return _isMultiThreaded;
	}

	@Override
	public boolean getAdaptorsDispatchRequestsConcurrently() {
		return adaptorsDispatchRequestsConcurrently();
	}

	public final boolean isConcurrentRequestHandlingEnabled() {
		if (_isMultiThreaded && _allowsConcurrentRequestHandling) {
			return true;
		}
		return false;
	}

	public Object requestHandlingLock() {
		if (_isMultiThreaded && !_allowsConcurrentRequestHandling) {
			return _globalLock;
		}
		return null;
	}

	@Deprecated
	public static final boolean licensingAllowsMultipleInstances() {
		return true;
	}

	@Deprecated
	public static final boolean licensingAllowsMultipleThreads() {
		return true;
	}

	@Deprecated
	public static final long licensedRequestWindow() {
		return 0L;
	}

	@Deprecated
	public static final int licensedRequestLimit() {
		return 0;
	}

	public static final void _setChecksForSpecialHeaders(final boolean flag) {
		_checksForSpecialHeaders = flag;
	}

	public static final boolean _checksForSpecialHeaders() {
		return _checksForSpecialHeaders;
	}

	@Deprecated
	public void setLoadFrameworks(final NSArray anArray) {
		WOProperties.TheLoadFrameworks = anArray;
	}

	public NSArray loadFrameworks() {
		if (WOProperties.TheLoadFrameworks == null) {
			final String aLoadFrameworkString = NSProperties.getProperty(WOProperties._LoadFrameworksKey);
			NSArray aLoadFrameworks = null;
			try {
				aLoadFrameworks = NSPropertyListSerialization.arrayForString(aLoadFrameworkString);
			} catch (final Exception e) {
				final StringBuilder exceptionString = new StringBuilder(
						"<WOApplication> Exception occurred while reading Properties for '")
						.append(WOProperties._LoadFrameworksKey).append("'. ");
				if (e instanceof NullPointerException) {
					exceptionString.append("The field is missing from the Properties files. ");
				} else {
					exceptionString.append("You have a format error in your Properties or WODefaultProperties file. ");
				}
				throw new NSForwardException(e, exceptionString.toString());
			}
			setLoadFrameworks(aLoadFrameworks);
		}
		return WOProperties.TheLoadFrameworks;
	}

	public void setProjectSearchPath(final NSArray<String> aPathArray) {
		WOProperties.setProjectSearchPath(aPathArray);
	}

	public NSArray<String> projectSearchPath() {
		return WOProperties.projectSearchPath();
	}

	@Deprecated
	public void setAdditionalAdaptors(final NSArray<NSDictionary<String, Object>> anAdaptorArray) {
		WOProperties.TheAdditionalAdaptors = anAdaptorArray;
	}

	public NSArray<NSDictionary<String, Object>> additionalAdaptors() {
		if (WOProperties.TheAdditionalAdaptors == null) {
			final String aAdditionalAdaptorsString = NSProperties.getProperty(WOProperties._AdditionalAdaptorsKey);
			NSArray<NSDictionary<String, Object>> aAdditionalAdaptors = null;
			try {
				aAdditionalAdaptors = NSPropertyListSerialization.arrayForString(aAdditionalAdaptorsString);
			} catch (final Exception e) {
				final StringBuilder exceptionString = new StringBuilder(
						"<WOApplication> Exception occurred while reading WOProperties for '")
						.append(WOProperties._AdditionalAdaptorsKey).append("'. ");
				if (e instanceof NullPointerException) {
					exceptionString.append("The field is missing from the WOProperties files. ");
				} else {
					exceptionString
							.append("You have a format error in your WOProperties or WODefaultProperties file. ");
				}
				throw new NSForwardException(e, exceptionString.toString());
			}
			setAdditionalAdaptors(aAdditionalAdaptors);
		}
		return WOProperties.TheAdditionalAdaptors;
	}

	@Override
	public ArrayList<HashMap<String, Object>> getAdditionalAdaptors() {
		final ArrayList<HashMap<String, Object>> clone = new ArrayList<>();
		for (final NSDictionary<String, Object> nsDictionary : additionalAdaptors()) {
			clone.add(nsDictionary.hashMap());
		}
		return clone;
	}

	public static boolean _isDebuggingEnabled() {
		return NSLog.debugLoggingAllowedForLevel(1);
	}

	public boolean isDebuggingEnabled() {
		return _isDebuggingEnabled();
	}

	@Deprecated
	public void setDirectConnectEnabled(final boolean aBool) {
		WOProperties.TheDirectConnectEnabledFlag = aBool;
		WOProperties.isTheDirectConnectEnabledFlagSet = true;
	}

	public boolean isDirectConnectEnabled() {
		if (!WOProperties.isTheDirectConnectEnabledFlagSet) {
			final boolean aFlag = Boolean.parseBoolean(NSProperties.getProperty(WOProperties._DirectConnectEnabledKey));
			setDirectConnectEnabled(aFlag);
		}
		return WOProperties.TheDirectConnectEnabledFlag;
	}

	private static void _setCachingEnabled(final boolean aBool) {
		WOProperties.TheCachingFlag = aBool;
		WOProperties.isTheCachingFlagSet = true;
	}

	private static boolean _isCachingEnabled() {
		if (!WOProperties.isTheCachingFlagSet) {
			final boolean aFlag = Boolean.parseBoolean(NSProperties.getProperty(WOProperties._CachingEnabledKey));
			_setCachingEnabled(aFlag);
		}
		return WOProperties.TheCachingFlag;
	}

	public void setCachingEnabled(final boolean aBool) {
		_setCachingEnabled(aBool);
	}

	public boolean isCachingEnabled() {
		return _isCachingEnabled();
	}

	@Override
	public boolean getIsCachingEnabled() {
		return isCachingEnabled();
	}

	public void setMonitorEnabled(final boolean aBool) {
		WOProperties.TheMonitorEnabledFlag = aBool;
		WOProperties.isTheMonitorEnabledFlagSet = true;
	}

	public boolean isMonitorEnabled() {
		if (!WOProperties.isTheMonitorEnabledFlagSet) {
			final boolean aFlag = Boolean.parseBoolean(NSProperties.getProperty(WOProperties._MonitorEnabledKey));
			setMonitorEnabled(aFlag);
		}
		return WOProperties.TheMonitorEnabledFlag;
	}

	@Override
	public boolean getIsMonitorEnabled() {
		return isMonitorEnabled();
	}

	public void setIncludeCommentsInResponses(final boolean aBool) {
		WOProperties.TheIncludeCommentsInResponseFlag = aBool;
		WOProperties.isTheIncludeCommentsInResponseFlagSet = true;
	}

	public boolean includeCommentsInResponses() {
		if (!WOProperties.isTheIncludeCommentsInResponseFlagSet) {
			final boolean aFlag = Boolean
					.parseBoolean(NSProperties.getProperty(WOProperties._IncludeCommentsInResponseKey));
			setIncludeCommentsInResponses(aFlag);
		}
		return WOProperties.TheIncludeCommentsInResponseFlag;
	}

	@Override
	public boolean getIncludeCommentsInResponses() {
		return includeCommentsInResponses();
	}

	@Deprecated
	public void setPort(final Number port) {
		WOProperties.ThePrimaryPort = port;
	}

	public Number port() {
		final Number aValue = Integer.valueOf(NSProperties.getProperty(WOProperties._PortKey));
		if (WOProperties.ThePrimaryPort == null) {
			setPort(aValue);
		}
		if (WOProperties.ThePrimaryPort.intValue() != aValue.intValue()) {
			WOProperties.ThePrimaryPort = aValue;
		}
		return WOProperties.ThePrimaryPort;
	}

	@Override
	public int getPort() {
		return port().intValue();
	}

	public void _setHost(final String host) {
		WOProperties.TheHost = host;
		try {
			_hostAddress = InetAddress.getByName(host);
		} catch (final UnknownHostException uhe) {
		}
	}

	public String host() {
		if (WOProperties.TheHost == null) {
			WOProperties.TheHost = NSProperties.getProperty(WOProperties._HostKey);
		}
		return WOProperties.TheHost;
	}

	@Override
	public String getHost() {
		return host();
	}

	public void _setHostAddress(final InetAddress host) {
		_hostAddress = host;
		WOProperties.TheHost = _hostAddress.getHostName();
	}

	public InetAddress hostAddress() {
		if (_hostAddress == null) {
			final String hostName = host();
			if (hostName != null) {
				try {
					_hostAddress = InetAddress.getByName(hostName);
				} catch (final UnknownHostException uhe) {
				}
			}
		}
		return _hostAddress;
	}

	@Override
	public String getHostAddress() {
		final InetAddress addr = hostAddress();
		return addr != null ? addr.toString() : "";
	}

	@Deprecated
	public void setWorkerThreadCount(final Number threadCount) {
		WOProperties.TheWorkerThreadCount = threadCount.intValue();
	}

	@Deprecated
	public Number workerThreadCount() {
		if (WOProperties.TheWorkerThreadCount == -1) {
			final Integer aValue = Integer.valueOf(NSProperties.getProperty(WOProperties._WorkerThreadCountKey));
			setWorkerThreadCount(aValue);
		}
		return _NSUtilities.IntegerForInt(WOProperties.TheWorkerThreadCount);
	}

	public void setWorkerThreadCountMin(final Number threadCount) {
		WOProperties.TheWorkerThreadCountMin = threadCount.intValue();
	}

	public Number workerThreadCountMin() {
		if (WOProperties.TheWorkerThreadCountMin == -1) {
			final Integer aValue = Integer.valueOf(NSProperties.getProperty(WOProperties._WorkerThreadCountMinKey));
			setWorkerThreadCountMin(aValue);
		}
		return _NSUtilities.IntegerForInt(WOProperties.TheWorkerThreadCountMin);
	}

	@Override
	public int getWorkerThreadCountMin() {
		return workerThreadCountMin().intValue();
	}

	public void setWorkerThreadCountMax(final Number threadCount) {
		WOProperties.TheWorkerThreadCountMax = threadCount.intValue();
	}

	public Number workerThreadCountMax() {
		if (WOProperties.TheWorkerThreadCountMax == -1) {
			final Integer aValue = Integer.valueOf(NSProperties.getProperty(WOProperties._WorkerThreadCountMaxKey));
			setWorkerThreadCountMax(aValue);
		}
		return _NSUtilities.IntegerForInt(WOProperties.TheWorkerThreadCountMax);
	}

	@Override
	public int getWorkerThreadCountMax() {
		return workerThreadCountMax().intValue();
	}

	@Deprecated
	public void setSocketCacheSize(final Number socketCacheSize) {
		WOProperties.TheSocketCacheSize = socketCacheSize.intValue();
	}

	@Deprecated
	public Number socketCacheSize() {
		if (WOProperties.TheSocketCacheSize == -1) {
			final Integer aValue = Integer.valueOf(NSProperties.getProperty(WOProperties._SocketCacheSizeKey));
			setSocketCacheSize(aValue);
		}
		return _NSUtilities.IntegerForInt(WOProperties.TheSocketCacheSize);
	}

	@Deprecated
	public void setSocketMonitorSleepTime(final Number socketMonitorSleepTime) {
		WOProperties.TheSocketMonitorSleepTime = socketMonitorSleepTime.intValue();
	}

	@Deprecated
	public Number socketMonitorSleepTime() {
		if (WOProperties.TheSocketMonitorSleepTime == -1) {
			final Integer aValue = Integer.valueOf(NSProperties.getProperty(WOProperties._SocketMonitorSleepTimeKey));
			setSocketMonitorSleepTime(aValue);
		}
		return _NSUtilities.IntegerForInt(WOProperties.TheSocketMonitorSleepTime);
	}

	@Deprecated
	public void setMaxSocketIdleTime(final Number maxSocketIdleTime) {
		WOProperties.TheMaxSocketIdleTime = maxSocketIdleTime.intValue();
	}

	public Number maxSocketIdleTime() {
		if (WOProperties.TheMaxSocketIdleTime == -1) {
			final Number aValue = Integer.valueOf(NSProperties.getProperty(WOProperties._MaxSocketIdleTimeKey));
			setMaxSocketIdleTime(aValue);
		}
		return Integer.valueOf(WOProperties.TheMaxSocketIdleTime);
	}

	@Override
	public int getMaxSocketIdleTime() {
		return maxSocketIdleTime().intValue();
	}

	@Deprecated
	public void setListenQueueSize(final Number listenQueueSize) {
		WOProperties.TheListenQueueSize = listenQueueSize;
	}

	public Number listenQueueSize() {
		if (WOProperties.TheListenQueueSize == null) {
			final Number aValue = Integer.valueOf(NSProperties.getProperty(WOProperties._ListenQueueSizeKey));
			setListenQueueSize(aValue);
		}
		return WOProperties.TheListenQueueSize;
	}

	@Override
	public int getListenQueueSize() {
		return listenQueueSize().intValue();
	}

	private void _setLifebeatEnabled(final boolean aBool) {
		WOProperties.TheLifebeatEnabledFlag = aBool;
		WOProperties.isTheLifebeatEnabledFlagSet = true;
	}

	public boolean lifebeatEnabled() {
		if (!WOProperties.isTheLifebeatEnabledFlagSet) {
			final String aValue = NSProperties.getProperty(WOProperties._LifebeatEnabledKey);
			_setLifebeatEnabled(Boolean.parseBoolean(aValue));
		}
		return WOProperties.TheLifebeatEnabledFlag;
	}

	@Deprecated
	public boolean monitorEnabled() {
		return isMonitorEnabled();
	}

	private void _setOutputPath(final String aString) {
		WOProperties.TheOutputPath = aString;
	}

	public String outputPath() {
		if (WOProperties.TheOutputPath == null) {
			final String aValue = NSProperties.getProperty(WOProperties._OutputPathKey);
			_setOutputPath(aValue);
		}
		return WOProperties.TheOutputPath;
	}

	@Override
	public String getOutputPath() {
		return outputPath();
	}

	protected void _setLifebeatDestinationPort(final int anInt) {
		WOProperties.TheLifebeatDestinationPort = anInt;
	}

	public int lifebeatDestinationPort() {
		if (WOProperties.TheLifebeatDestinationPort == -1) {
			final int aValue = Integer.parseInt(NSProperties.getProperty(WOProperties._LifebeatDestinationPortKey));
			_setLifebeatDestinationPort(aValue);
		}
		return WOProperties.TheLifebeatDestinationPort;
	}

	@Override
	public int getLifebeatDestinationPort() {
		return lifebeatDestinationPort();
	}

	private void _setLifebeatInterval(final int anInt) {
		WOProperties.TheLifebeatInterval = anInt;
	}

	public int lifebeatInterval() {
		if (WOProperties.TheLifebeatInterval == -1) {
			final int aValue = Integer.parseInt(NSProperties.getProperty(WOProperties._LifebeatIntervalKey));
			_setLifebeatInterval(aValue);
		}
		return WOProperties.TheLifebeatInterval;
	}

	@Override
	public int getLifebeatInterval() {
		return lifebeatInterval();
	}

	@Deprecated
	public void setMonitorHost(final String aString) {
	}

	@Deprecated
	public String monitorHost() {
		return __localhost;
	}

	public void setRecordingPath(final String aString) {
		WOProperties.TheRecordingPath = aString;
	}

	public String recordingPath() {
		if (WOProperties.TheRecordingPath == null) {
			final String aValue = NSProperties.getProperty(WOProperties._RecordingPathKey);
			setRecordingPath(aValue);
		}
		return WOProperties.TheRecordingPath;
	}

	public void setAutoOpenInBrowser(final boolean aBool) {
		WOProperties.TheAutoOpenBrowserFlag = aBool;
		WOProperties.isTheAutoOpenBrowserFlagSet = true;
	}

	public boolean autoOpenInBrowser() {
		if (!WOProperties.isTheAutoOpenBrowserFlagSet) {
			final String aValue = NSProperties.getProperty(WOProperties._AutoOpenBrowserKey);
			setAutoOpenInBrowser(Boolean.parseBoolean(aValue));
		}
		return WOProperties.TheAutoOpenBrowserFlag;
	}

	public void setAutoOpenClientApplication(final boolean aBool) {
		WOProperties.TheAutoOpenClientApplicationFlag = aBool;
		WOProperties.isTheAutoOpenClientApplicationFlagSet = true;
	}

	public boolean autoOpenClientApplication() {
		if (!WOProperties.isTheAutoOpenClientApplicationFlagSet) {
			final String aValue = NSProperties.getProperty(WOProperties._AutoOpenClientApplicationKey);
			setAutoOpenClientApplication(Boolean.parseBoolean(aValue));
		}
		return WOProperties.TheAutoOpenClientApplicationFlag;
	}

	public boolean getAutoOpenClientApplication() {
		return autoOpenClientApplication();
	}

	public void setSMTPHost(final String aString) {
		WOProperties.TheSMTPHost = aString;
	}

	public String SMTPHost() {
		if (WOProperties.TheSMTPHost == null) {
			final String aValue = NSProperties.getProperty(WOProperties._SMTPHostKey);
			setSMTPHost(aValue);
		}
		return WOProperties.TheSMTPHost;
	}

	@Deprecated
	public void setAdaptor(final String aString) {
		WOProperties.ThePrimaryAdaptorName = aString;
	}

	public String adaptor() {
		if (WOProperties.ThePrimaryAdaptorName == null) {
			final String aValue = NSProperties.getProperty(WOProperties._AdaptorKey);
			setAdaptor(aValue);
		}
		return WOProperties.ThePrimaryAdaptorName;
	}

	@Deprecated
	public void setComponentRequestHandlerKey(final String aString) {
		WOProperties.TheComponentRequestHandlerKey = aString;
	}

	public String componentRequestHandlerKey() {
		if (WOProperties.TheComponentRequestHandlerKey == null) {
			final String aValue = NSProperties.getProperty(WOProperties._ComponentRequestHandlerKey);
			setComponentRequestHandlerKey(aValue);
		}
		return WOProperties.TheComponentRequestHandlerKey;
	}

	public String getComponentRequestHandlerKey() {
		return componentRequestHandlerKey();
	}

	@Deprecated
	public void setDirectActionRequestHandlerKey(final String aString) {
		WOProperties.TheDirectActionRequestHandlerKey = aString;
	}

	public String directActionRequestHandlerKey() {
		if (WOProperties.TheDirectActionRequestHandlerKey == null) {
			final String aValue = NSProperties.getProperty(WOProperties._DirectActionRequestHandlerKey);
			setDirectActionRequestHandlerKey(aValue);
		}
		return WOProperties.TheDirectActionRequestHandlerKey;
	}

	public String streamActionRequestHandlerKey() {
		return NSProperties.getProperty(WOProperties.TheStreamActionRequestHandlerKey);
	}

	@Deprecated
	public void setResourceRequestHandlerKey(final String aString) {
		WOProperties.TheResourceRequestHandlerKey = aString;
	}

	public String resourceRequestHandlerKey() {
		if (WOProperties.TheResourceRequestHandlerKey == null) {
			final String aValue = NSProperties.getProperty(WOProperties._ResourceRequestHandlerKey);
			setResourceRequestHandlerKey(aValue);
		}
		return WOProperties.TheResourceRequestHandlerKey;
	}

	@Deprecated
	public void setWebServiceRequestHandlerKey(final String aString) {
		WOProperties.TheWebServiceRequestHandlerKey = aString;
	}

	public String webServiceRequestHandlerKey() {
		if (WOProperties.TheWebServiceRequestHandlerKey == null) {
			final String aValue = NSProperties.getProperty(WOProperties._WebServiceRequestHandlerKey);
			setWebServiceRequestHandlerKey(aValue);
		}
		return WOProperties.TheWebServiceRequestHandlerKey;
	}

	public String ajaxRequestHandlerKey() {
		if (WOProperties.TheAjaxRequestHandlerKey == null) {
			final String aValue = NSProperties.getProperty(WOProperties._AjaxRequestHandlerKey);
			WOProperties.TheAjaxRequestHandlerKey = aValue;
		}
		return WOProperties.TheAjaxRequestHandlerKey;
	}

	public void setSessionStoreClassName(final String aString) {
		if (aString != null) {
			final WOSessionStore sessionStore = (WOSessionStore) _instanceOfNamedClassAssignableFrom(aString,
					WOSessionStore.class, WOServerSessionStore.class);
			WOProperties.TheSessionStoreClassName = sessionStore.getClass().getName();
			setSessionStore(sessionStore);
		}
	}

	public String sessionStoreClassName() {
		return WOProperties.TheSessionStoreClassName;
	}

	public WODynamicURL newDynamicURL() {
		return new WODynamicURL();
	}

	public WODynamicURL newDynamicURL(final String url) throws WOURLFormatException {
		return new WODynamicURL(url);
	}

	public void setFrameworksBaseURL(final String aString) {
		WOProperties.TheFrameworksBaseURL = aString;
	}

	public String frameworksBaseURL() {
		if (!_cgiAdaptorURLParsed) {
			_parseCGIAdaptorURL();
		}
		return WOProperties.TheFrameworksBaseURL;
	}

	public void setCGIAdaptorURL(final String aString) {
		WOProperties.TheCGIAdaptorURL = aString;
	}

	public String cgiAdaptorURL() {
		if (!_cgiAdaptorURLParsed) {
			_parseCGIAdaptorURL();
		}
		return WOProperties.TheCGIAdaptorURL;
	}

	@Override
	public String getCGIAdaptorURL() {
		return cgiAdaptorURL();
	}

	public void setApplicationBaseURL(final String aString) {
		WOProperties.TheApplicationBaseURL = aString;
	}

	public String applicationBaseURL() {
		if (!_cgiAdaptorURLParsed) {
			_parseCGIAdaptorURL();
		}
		return WOProperties.TheApplicationBaseURL;
	}

	@Override
	public String getApplicationBaseURL() {
		return applicationBaseURL();
	}

	private static final String[] _applicationExtensions = { ".exe", ".dll" };

	private String _adaptorName;

	private String _adaptorPath;

	private static final String __hostDelimiter = "//";

	private static final String __localhost = "localhost";

	private static final String __localAddress = "127.0.0.1";

	private String _documentRoot;

	public String[] adaptorExtensions() {
		return _applicationExtensions;
	}

	public String applicationExtension() {
		return ".woa";
	}

	public String getApplicationExtension() {
		return applicationExtension();
	}

	public String adaptorName() {
		if (!_cgiAdaptorURLParsed) {
			_parseCGIAdaptorURL();
		}
		return _adaptorName.length() > 0 ? _adaptorName : "WebObjects";
	}

	public String adaptorPath() {
		if (!_cgiAdaptorURLParsed) {
			_parseCGIAdaptorURL();
		}
		return _adaptorPath.length() > 0 ? _adaptorPath : "/cgi-bin/WebObjects";
	}

	protected void _parseCGIAdaptorURL() {
		String url = System.getProperty("application.cgiAdaptorUrl", "");
		if (url.length() == 0) {
			url = NSProperties.getProperty(WOProperties._CGIAdaptorURLKey);
		} else {
			NSProperties._setProperty(WOProperties._CGIAdaptorURLKey, url);
		}
		try {
			if (url.charAt(url.length() - 1) == '/') {
				url = url.substring(0, url.length() - 2);
				System.setProperty("application.cgiAdaptorUrl", url);
				NSProperties._setProperty(WOProperties._CGIAdaptorURLKey, url);
			}
			int i = url.indexOf(__localhost);
			int j = url.indexOf(__localAddress);
			if (i != -1) {
				_adaptorPath = url.substring(i + __localhost.length());
				url = url.substring(0, i) + host() + _adaptorPath;
			} else if (j != -1) {
				_adaptorPath = url.substring(j + __localAddress.length());
				url = url.substring(0, j) + hostAddress().getHostAddress() + _adaptorPath;
			} else {
				i = url.indexOf(__hostDelimiter);
				if (i <= 0) {
					throw new Exception();
				}
				j = url.indexOf('/', i + __hostDelimiter.length());
				if (j <= 0) {
					throw new Exception();
				}
				_adaptorPath = url.substring(j);
			}
			final int k = _adaptorPath.lastIndexOf('/');
			if (k >= 0) {
				_adaptorName = _adaptorPath.substring(k + 1);
			} else {
				_adaptorName = _adaptorPath;
			}
			final String directoryAlias = System.getProperty("application.directoryAlias", "/" + _adaptorName);
			setApplicationBaseURL(directoryAlias);
			setFrameworksBaseURL(directoryAlias + "/Frameworks");
			setCGIAdaptorURL(url);
		} catch (final Exception exception) {
			url = "http://localhost/cgi-bin/WebObjects";
			System.setProperty("application.cgiAdaptorUrl", url);
			_parseCGIAdaptorURL();
			NSLog.err.appendln("The " + WOProperties._CGIAdaptorURLKey
					+ " has the wrong format. The application will use the default value " + url);
		}
		_cgiAdaptorURLParsed = true;
	}

	public String documentRoot() {
		if (_documentRoot.length() == 0) {
			_documentRoot = System.getProperty("application.documentRoot", "");
			if (_documentRoot.length() == 0) {
				final InputStream inputstream = application().resourceManager()
						.inputStreamForResourceNamed("WebServerConfig.plist", "JavaWebObjects", null);
				if (inputstream != null) {
					final String webServerConfig = _NSStringUtilities.stringFromInputStream(inputstream);
					if (webServerConfig != null) {
						_documentRoot = (String) ((NSDictionary) NSPropertyListSerialization
								.propertyListFromString(webServerConfig)).objectForKey("DocumentRoot");
					}
				}
			}
		}
		return _documentRoot.length() > 0 ? _documentRoot : "/Library/WebServer/Documents";
	}

	public void setAllowsConcurrentRequestHandling(final boolean aValue) {
		WOProperties.TheAllowsConcurrentRequestHandlingFlag = aValue;
		WOProperties.isTheAllowsConcurrentRequestHandlingFlagSet = true;
		_allowsConcurrentRequestHandling = aValue;
		EOObjectStore._resetAssertLock();
		if (!_allowsConcurrentRequestHandling) {
			EOObjectStore._suppressAssertLock();
		}
	}

	public boolean allowsConcurrentRequestHandling() {
		if (!WOProperties.isTheAllowsConcurrentRequestHandlingFlagSet) {
			final boolean aValue = Boolean
					.parseBoolean(NSProperties.getProperty(WOProperties._AllowsConcurrentRequestHandlingKey));
			setAllowsConcurrentRequestHandling(aValue);
		}
		return WOProperties.TheAllowsConcurrentRequestHandlingFlag;
	}

	@Override
	public boolean getAllowsConcurrentRequestHandling() {
		return allowsConcurrentRequestHandling();
	}

	public void setSessionTimeOut(final Number timeOut) {
		long aTimeOut = timeOut.longValue();
		if (aTimeOut < 0L) {
			aTimeOut = 0L;
		}
		WOProperties.TheSessionTimeOut = Double.valueOf(aTimeOut);
	}

	public void setDefaultUndoStackLimit(final int stackLimit) {
		WOProperties.TheDefaultUndoStackLimit = stackLimit < 0 ? 10 : stackLimit;
	}

	public Number sessionTimeOut() {
		if (WOProperties.TheSessionTimeOut == null) {
			long aValue = Integer.valueOf(NSProperties.getProperty(WOProperties._SessionTimeOutKey)).longValue();
			if (aValue < 0L) {
				aValue = 0L;
			}
			setSessionTimeOut(Double.valueOf(aValue));
		}
		return WOProperties.TheSessionTimeOut;
	}

	@Override
	public int getSessionTimeOut() {
		return sessionTimeOut().intValue();
	}

	public Number defaultUndoStackLimit() {
		if (WOProperties.TheDefaultUndoStackLimit == -1) {
			int aValue = Integer.parseInt(NSProperties.getProperty(WOProperties._DefaultUndoStackLimitKey));
			setDefaultUndoStackLimit(aValue);
			if (aValue < 0) {
				aValue = 10;
			}
			setDefaultUndoStackLimit(aValue);
		}
		return _NSUtilities.IntegerForInt(WOProperties.TheDefaultUndoStackLimit);
	}

	@Override
	public int getDefaultUndoStackLimit() {
		final Number limit = defaultUndoStackLimit();
		return limit != null ? limit.intValue() : WOProperties.TheDefaultUndoStackLimit;
	}

	protected WOAssociationFactoryRegistry createDefaultAssociationFactoryRegistry() {
		final WOAssociationFactoryRegistry result = new WOAssociationFactoryRegistry();
		result.setAssociationFactory("constant", new WOConstantAssociationFactory());
		result.setAssociationFactory("kvc", new WOKeyValueAssociationFactory());
		result.setAssociationFactory("var", new WOVariableAssociationFactory());
		return result;
	}

	public final WOAssociationFactory associationFactory() {
		return associationFactoryRegistry();
	}

	public WOAssociationFactoryRegistry associationFactoryRegistry() {
		if (_associationFactoryRegistry == null) {
			_associationFactoryRegistry = createDefaultAssociationFactoryRegistry();
		}
		return _associationFactoryRegistry;
	}

	protected WOMLDefaultNamespaceProvider createDefaultNamespaceProvider() {
		final WOMLDefaultNamespaceProvider defaultNamespaceProvider = new WOMLDefaultNamespaceProvider();
		defaultNamespaceProvider.addNamespace(new WOMLDefaultNamespace(null));
		WOMLWebObjectsNamespace webObjectsNamespace;
		defaultNamespaceProvider.addNamespace(webObjectsNamespace = new WOMLWebObjectsNamespace("wo"));
		defaultNamespaceProvider.addNamespace(new WOMLWebObjectsQualifierNamespace("woq"));
		defaultNamespaceProvider.setDefaultNamespace(webObjectsNamespace);
		return defaultNamespaceProvider;
	}

	public WOMLNamespaceProvider namespaceProvider() {
		return _namespaceProvider;
	}

	public void setNamespaceProvider(final WOMLNamespaceProvider value) {
		_namespaceProvider = value;
	}

	protected void _setLockDefaultEditingContext(final boolean aValue) {
		WOProperties.TheLockDefaultEditingContextFlag = aValue;
		WOProperties.isTheLockDefaultEditingContextFlagSet = true;
	}

	protected boolean _lockDefaultEditingContext() {
		if (!WOProperties.isTheLockDefaultEditingContextFlagSet) {
			boolean aFlag = true;
			final String flagValue = NSProperties.getProperty(WOProperties._LockDefaultEditingContextKey);
			if (flagValue != null) {
				aFlag = Boolean.parseBoolean(flagValue);
			}
			_setLockDefaultEditingContext(aFlag);
		}
		return WOProperties.TheLockDefaultEditingContextFlag;
	}

	public static class _EventLoggingEnabler implements EOEventCenter.EventRecordingHandler {
		@Override
		public void setLoggingEnabled(final boolean isLogging, final Class aClass) {
			WOApplication._IsEventLoggingEnabled = isLogging;
		}
	}

	public static boolean canAccessFieldsDirectly() {
		return true;
	}

	@Override
	public Object valueForKey(final String key) {
		return NSKeyValueCoding.DefaultImplementation.valueForKey(this, key);
	}

	@Override
	public void takeValueForKey(final Object value, final String key) {
		NSKeyValueCoding.DefaultImplementation.takeValueForKey(this, value, key);
	}

	@Override
	public Object handleQueryWithUnboundKey(final String key) {
		return NSKeyValueCoding.DefaultImplementation.handleQueryWithUnboundKey(this, key);
	}

	@Override
	public void handleTakeValueForUnboundKey(final Object value, final String key) {
		NSKeyValueCoding.DefaultImplementation.handleTakeValueForUnboundKey(this, value, key);
	}

	@Override
	public void unableToSetNullForKey(final String key) {
		NSKeyValueCoding.DefaultImplementation.unableToSetNullForKey(this, key);
	}

	@Override
	public Object valueForKeyPath(final String keyPath) {
		return NSKeyValueCodingAdditions.DefaultImplementation.valueForKeyPath(this, keyPath);
	}

	@Override
	public void takeValueForKeyPath(final Object value, final String keyPath) {
		NSKeyValueCodingAdditions.DefaultImplementation.takeValueForKeyPath(this, value, keyPath);
	}

	public void validationFailedWithException(final Throwable t, final Object value, final String keyPath,
			final WOComponent component,
			final WOSession session) {
		if (NSLog.debugLoggingAllowedForLevelAndGroups(2, 132L)) {
			NSLog.debug
					.appendln("Validation failed on an object [" + (value == null ? "null" : value.getClass().getName())
							+ "] with keypath = " + keyPath + " and exception: " + t.getMessage());
			if (NSLog.debugLoggingAllowedForLevelAndGroups(3, 132L)) {
				if (component != null) {
					NSLog.debug.appendln(component.toString());
				}
				if (session != null) {
					NSLog.debug.appendln(session.toString());
				}
				NSLog.debug.appendln(t);
			}
		}
	}

	@Deprecated
	public void lock() {
	}

	@Deprecated
	public void unlock() {
	}

	public boolean _isSupportedDevelopmentPlatform() {
		return _isDomesticSupportedDevelopmentPlatform() || _isForeignSupportedDevelopmentPlatform();
	}

	public boolean _isDomesticSupportedDevelopmentPlatform() {
		final String osName = System.getProperty("os.name");
		return osName != null && (osName.startsWith("Mac OS") || "Darwin".equals(osName) || "Rhapsody".equals(osName));
	}

	public boolean _isForeignSupportedDevelopmentPlatform() {
		final String osName = System.getProperty("os.name");
		return osName != null
				&& ("Windows 2000".equals(osName) || "Windows NT".equals(osName) || "Linux".equals(osName));
	}

	private static void _initWOApp(final boolean logDefaults) {
		WOProperties.initUserDefaultsKeys();
		WOProperties.initPropertiesFromWebServerConfig();
		WOProperties.initProgrammaticWODefaults();
		NSProperties.setPropertiesFromArgv(_argv);
		NSLog._setInInitPhase(true);
		_initWOOuputPath();
		WOProperties.initLanguageDictionary();
		NSLog._setInInitPhase(false);
		if (logDefaults) {
			WOProperties.printWODefaults();
		}
	}

	private static void _initWOOuputPath() {
		final String outputPath = NSProperties.getProperty(WOProperties._OutputPathKey);
		if (outputPath != null) {
			if ("/dev/null".equals(outputPath)) {
				System.setOut(new NSLog._DevNullPrintStream(System.out));
				System.setErr(new NSLog._DevNullPrintStream(System.err));
				((NSLog.PrintStreamLogger) NSLog.debug).setPrintStream(System.err);
				((NSLog.PrintStreamLogger) NSLog.out).setPrintStream(System.out);
				((NSLog.PrintStreamLogger) NSLog.err).setPrintStream(System.err);
			} else {
				final File outputFile = new File(outputPath);
				if (outputFile.exists()) {
					if (outputFile.isDirectory()) {
						NSLog.err.appendln("WOApplication Error: WOOutputPath " + outputPath + " is a directory.");
						return;
					}
					final NSTimestampFormatter formatter = new NSTimestampFormatter("%Y%m%d%H%M%S%F");
					final File renamedFile = new File(outputPath + "." + formatter.format(new NSTimestamp()));
					final boolean didRename = outputFile.renameTo(renamedFile);
					if (!didRename) {
						NSLog.err.appendln(
								"WOApplication Error: Failed to rename previously existing WOOutputPath file: "
										+ outputPath);
						return;
					}
					NSLog.err.appendln("WOApplication: Renamed previous WOOutputPath file to " + renamedFile.getPath());
				} else {
					try {
						final File outputDir = outputFile.getParentFile();
						if (!outputDir.exists() &&
								!outputDir.mkdirs()) {
							NSLog.err.appendln("WOApplication Error: WOOutputPath directory " + outputDir.getPath()
									+ " could not be created.");
							return;
						}
						final boolean result = outputFile.createNewFile();
						if (!result) {
							NSLog.err.appendln(
									"WOApplication Error: WOOutputPath " + outputPath + " could not be created.");
							return;
						}
					} catch (final IOException ioe) {
						NSLog.err.appendln(
								"WOApplication Error: WOOutputPath " + outputPath + " could not be created: " + ioe);
						if (NSLog.debugLoggingAllowedForLevelAndGroups(3, 4L)) {
							NSLog.err.appendln(ioe);
						}
						return;
					}
				}
				final PrintStream aStream = NSLog.printStreamForPath(outputPath);
				if (aStream != null) {
					System.setOut(aStream);
					System.setErr(aStream);
					((NSLog.PrintStreamLogger) NSLog.debug).setPrintStream(aStream);
					((NSLog.PrintStreamLogger) NSLog.out).setPrintStream(aStream);
					((NSLog.PrintStreamLogger) NSLog.err).setPrintStream(aStream);
				}
			}
		}
	}

	private Object _instanceOfNamedClassAssignableFrom(final String nameOfClassToInstantiate,
			final Class<?> assignableFrom,
			final Class<?> defaultClass) {
		final Class<?> classToInstantiate = _NSUtilities.classWithName(nameOfClassToInstantiate);
		Object newInstance = null;
		if (classToInstantiate != null) {
			if (assignableFrom.isAssignableFrom(classToInstantiate)) {
				newInstance = _NSUtilities.instantiateObject(classToInstantiate, null, null, true,
						isDebuggingEnabled());
			} else {
				NSLog.err.appendln("<WOApplication> " + nameOfClassToInstantiate + " is not a valid (" + defaultClass
						+ ") class. Using default (" + defaultClass + ").");
				newInstance = _NSUtilities.instantiateObject(defaultClass, null, null, true, isDebuggingEnabled());
			}
		} else {
			NSLog.err.appendln("<WOApplication> " + nameOfClassToInstantiate + " is an unknown class. Using default ("
					+ defaultClass + ").");
			newInstance = _NSUtilities.instantiateObject(defaultClass, null, null, true, isDebuggingEnabled());
		}
		return newInstance;
	}

	Number _refuseNewSessionsTime() {
		int delayTime;
		final int timeout = sessionTimeOut().intValue();
		final int active = activeSessionsCount();
		if (active > 0) {
			final int minActive = minimumActiveSessionsCount();
			delayTime = timeout * (active - minActive) / active;
			if (delayTime > timeout / 4) {
				delayTime = timeout / 4;
			}
		} else {
			delayTime = timeout / 4;
		}
		if (delayTime < 15) {
			delayTime = 15;
		}
		return Integer.valueOf(delayTime);
	}

	public WOResponse responseForComponentWithName(final String name, final Map<String, Object> bindings,
			final Map<String, ? extends List<String>> headers, final Map<String, Object> userInfo,
			final String uriPrefix,
			final String appName) {
		NSMutableDictionary<String, Object> info = null;
		if (userInfo != null) {
			info = new NSMutableDictionary<>(userInfo);
		} else {
			info = new NSMutableDictionary<>(2);
		}
		info.setObjectForKey(name, "Component");
		if (bindings != null) {
			info.setObjectForKey(bindings, "Bindings");
		}
		String anUriPrefix = uriPrefix;
		if (anUriPrefix == null) {
			anUriPrefix = cgiAdaptorURL();
			if (anUriPrefix == null) {
				anUriPrefix = "/cgi-bin/WebObjects";
			}
		}
		final String anAppName = appName != null ? appName : name();
		final StringBuffer sb = new StringBuffer(anUriPrefix);
		sb.append('/');
		sb.append(anAppName);
		if (anAppName.indexOf(".woa") == -1) {
			sb.append(".woa");
		}
		sb.append('/');
		sb.append(directActionRequestHandlerKey());
		sb.append("/_component");
		final String uri = new String(sb);
		final WORequest request = application().createRequest("GET", uri, "HTTP/1.0", headers, null, info);
		return dispatchRequest(request);
	}

	public WOResponse responseForDirectActionWithNameAndClass(final String actionName, final String className,
			final Map<String, Object> formValueDict, final InputStream contentStream,
			final Map<String, ? extends List<String>> headers,
			final Map<String, Object> userInfo, final String uriPrefix, final String appName) {
		String method = "POST";
		String anUriPrefix = uriPrefix;
		if (anUriPrefix == null) {
			anUriPrefix = cgiAdaptorURL();
			if (anUriPrefix == null) {
				anUriPrefix = "/cgi-bin/WebObjects";
			}
		}
		final String anAppName = appName != null ? appName : name();
		final StringBuffer sb = new StringBuffer(anUriPrefix);
		sb.append('/');
		sb.append(anAppName);
		sb.append('/');
		sb.append(directActionRequestHandlerKey());
		sb.append('/');
		if (className != null) {
			sb.append(className);
		}
		sb.append('/');
		if (actionName != null) {
			sb.append(actionName);
		}
		if (formValueDict != null) {
			sb.append('?');
			sb.append(WOCGIFormValues.getInstance().encodeAsCGIFormValues(formValueDict));
			method = "GET";
		}
		final String uri = new String(sb);
		NSData contentData = null;
		if (contentStream != null) {
			int contentLength = 1;
			final List<String> contentLengthArray = headers.get("content-length");
			if (contentLengthArray != null && contentLengthArray.size() > 0) {
				try {
					contentLength = Integer.parseInt(contentLengthArray.get(0));
				} catch (final NumberFormatException nfe) {
					NSLog.err.appendln(
							"<WOApplication> responseForDirectActionWithNameAndClass: illegal content-length header: "
									+ contentLengthArray.get(0));
				}
			}
			try {
				contentData = new NSData(contentStream, contentLength);
			} catch (final IOException ioe) {
				NSLog.err.appendln(
						"<WOApplication> responseForDirectActionWithNameAndClass: exception getting content: " + ioe);
			}
		}
		final WORequest request = application().createRequest(method, uri, "HTTP/1.0", headers, contentData, userInfo);
		return dispatchRequest(request);
	}

	public String getAgentID() {
		if (_agentID == null) {
			_agentID = System.getProperty("agentid");
			if (_agentID == null) {
				_agentID = WOUniqueIDGenerator.sharedInstance().longUniqueID();
			}
		}
		return _agentID;
	}

	public String sessionIdKey() {
		return "wosid";
	}

	public String instanceIdKey() {
		return "woinst";
	}

	public MBeanServer getMBeanServer() throws IllegalAccessException {
		if (_mbs == null) {
			_mbs = ManagementFactory.getPlatformMBeanServer();
			if (_mbs == null) {
				throw new IllegalAccessException(
						"Error: PlatformMBeanServer could not be accessed via ManagementFactory.");
			}
		}
		return _mbs;
	}

	@Override
	public String getJMXDomain() {
		if (_jmxDomain == null) {
			_jmxDomain = host() + "." + name() + "." + port();
		}
		return _jmxDomain;
	}

	public void setJMXDomain(final String aName) {
		_jmxDomain = aName;
	}

	public void registerMBean(final Object aMBean, final ObjectName aName) {
		try {
			getMBeanServer().registerMBean(aMBean, aName);
		} catch (IllegalAccessException | InstanceAlreadyExistsException | MBeanRegistrationException
				| NotCompliantMBeanException e) {
			NSLog._conditionallyLogPrivateException(e);
		}
	}

	public void registerMBean(final Object aMBean, final String aDomainName, final String aMBeanName)
			throws IllegalArgumentException {
		if (aMBean == null) {
			throw new IllegalArgumentException(
					"Error: null value for MBean object cannot be regiestered to the PlatformMbeanServer.");
		}
		if (aMBeanName == null) {
			throw new IllegalArgumentException("Error: MBean name can not be null.");
		}
		ObjectName name = null;
		final String domainName = aDomainName == null ? getJMXDomain() : aDomainName;
		try {
			name = new ObjectName(domainName + ": name=" + aMBeanName);
		} catch (MalformedObjectNameException | NullPointerException e) {
			e.printStackTrace();
		}
		registerMBean(aMBean, name);
	}

	public void unregisterMBean(final ObjectName aName) {
		if (aName != null) {
			try {
				getMBeanServer().unregisterMBean(aName);
			} catch (IllegalAccessException | InstanceNotFoundException | MBeanRegistrationException e) {
				e.printStackTrace();
			}
		}
	}

	public String[] launchArguments() {
		return _argv;
	}
}