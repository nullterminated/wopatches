package com.webobjects.foundation.development;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSForwardException;
import com.webobjects.foundation.NSLog;
import com.webobjects.foundation.NSNestedProperties;
import com.webobjects.foundation.NSPropertyListSerialization;

public class JigsawBundleAdaptor implements NSBundleAdaptorProvider {
	private static final String JIGSAW_SEARCH_PATH = "META-INF/webobjects/Resources/Info.plist";

	@Override
	public String adaptorBundlePath(final FileSystem fs, final String bundlePath) {
		return bundlePath.startsWith("jrt:")
				? "jrt:/modules" + bundlePath.substring(4, bundlePath.length() - JIGSAW_SEARCH_PATH.length())
				: bundlePath;
	}

	@Override
	public NSBundleInfo bundleInfoFromFileSystem(final FileSystem fs, final Path fsBundlePath) {
		try {
			// NSLog.out.appendln("Files in path: " + "/");
			// Files.walk(fs.getPath("/")).forEach(NSLog.out::appendln);

			final Path infoPath = fsBundlePath.resolve(JIGSAW_SEARCH_PATH);
			final String infoString = Files.readAllLines(infoPath).stream()
					.collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
			final NSDictionary dict = NSPropertyListSerialization.dictionaryForString(infoString);
			return NSBundleInfo.forDictionary(dict);
		} catch (final IOException e) {
			throw NSForwardException._runtimeExceptionForThrowable(e);
		}
	}

	@Override
	public Stream<String> classNamesForFileSystem(final FileSystem fs, final Path fsBundlePath) {
		final List<Path> resourcePaths = resourcePathsForFileSystem(fs, fsBundlePath);
		try {
			return Files.walk(fsBundlePath)
					// only files
					.filter(Files::isRegularFile)
					// not java client files
					.filter(path -> resourcePaths.stream().noneMatch(rpath -> path.startsWith(rpath)))
					// that are .classes
					.filter(path -> path.getFileName().toString().endsWith(".class")).map(Path::toString)
					// trim off .class extension and leading /
					.map(name -> name.substring(fsBundlePath.toString().length(), name.length() - ".class".length()))
					// replace path separators with .
					.map(name -> name.replace('/', '.'))
					// return the interned string
					.map(String::intern);
		} catch (final IOException e) {
			throw NSForwardException._runtimeExceptionForThrowable(e);
		}
	}

	@Override
	public Path fsBundlePath(final FileSystem fs, final String bundlePath) {
		return bundlePath.startsWith("jrt:") ? fs.getPath(bundlePath.substring(4)) : fs.getPath("/");
	}

	@Override
	public boolean isAdaptable(final FileSystem fs, final String bundlePath) {
		final boolean result;
		/*
		 * A Jigsaw bundle may be a modular jar on the module path, or it may be part of
		 * a jlinked application using a jrt:/ uri. We can support both with a single
		 * adaptor.
		 */
		final boolean isJavaRuntimeImage = bundlePath.startsWith("jrt:");
		final boolean isMavenProject = bundlePath.contains("target/classes");
		if (isJavaRuntimeImage && bundlePath.endsWith(JIGSAW_SEARCH_PATH)) {
			result = true;
		} else if (!isMavenProject) {
			final Path infoPath = fs.getPath(JIGSAW_SEARCH_PATH);
			result = Files.exists(infoPath) && Files.isReadable(infoPath);
		} else {
			result = false;
		}
		return result;
	}

	@Override
	public Properties propertiesForFileSystem(final FileSystem fs, final Path fsBundlePath) {
		final Path propsPath = fsBundlePath.resolve("META-INF/webobjects/Resources/Properties");
		final NSNestedProperties nested = new NSNestedProperties(null);
		try (InputStream propertiesStream = Files.newInputStream(propsPath)) {
			nested.load(propertiesStream);
		} catch (final NoSuchFileException e) {
			if (NSLog._debugLoggingAllowedForLevelAndGroups(NSLog.DebugLevelDetailed,
					NSLog.DebugGroupResources | NSLog.DebugGroupWebObjects)) {
				NSLog.debug.appendln(e.getMessage());
			}
		} catch (final IOException e) {
			throw NSForwardException._runtimeExceptionForThrowable(e);
		}
		return nested;
	}

	@Override
	public List<Path> resourcePathsForFileSystem(final FileSystem fs, final Path fsBundlePath) {
		return Arrays.asList(fsBundlePath.resolve("META-INF/webobjects/Resources"),
				fsBundlePath.resolve("META-INF/webobjects/WebServerResources"));
	}

}
