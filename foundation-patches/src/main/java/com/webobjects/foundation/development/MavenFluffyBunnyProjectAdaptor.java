package com.webobjects.foundation.development;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSForwardException;
import com.webobjects.foundation.NSNestedProperties;
import com.webobjects.foundation.NSPropertyListSerialization;
import com.webobjects.foundation.development.NSBundleAdaptorProvider;
import com.webobjects.foundation.development.NSBundleInfo;

public class MavenFluffyBunnyProjectAdaptor implements NSBundleAdaptorProvider {

	@Override
	public String adaptorBundlePath(final FileSystem fs, final String bundlePath) {
		return locateAdaptorBundlePath(fs, bundlePath).get();
	}

	@Override
	public NSBundleInfo bundleInfoFromFileSystem(final FileSystem fs, final Path fsBundlePath) {
		try {
			final Path infoPath = fsBundlePath.resolve("Resources/Info.plist");
			final String infoString = Files.readAllLines(infoPath).stream()
					.collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
			final NSDictionary dict = NSPropertyListSerialization.dictionaryForString(infoString);
			return NSBundleInfo.forDictionary(dict);
		} catch (final IOException e) {
			throw new RuntimeException("Exception loading Info.plist", e);
		}

	}

	@Override
	public Stream<String> classNamesForFileSystem(final FileSystem fs, final Path fsBundlePath) {
		final Path walkPath = fsBundlePath.resolve("target/classes/");
		try {
			return Files.walk(walkPath).filter(Files::isRegularFile)
					.filter(p -> p.getFileName().toString().endsWith(".class")).map(Path::toString)
					.map(name -> name.substring(walkPath.toString().length() + 1, name.length() - ".class".length()))
					.map(name -> name.replace('/', '.')).map(String::intern);
		} catch (final IOException e) {
			throw NSForwardException._runtimeExceptionForThrowable(e);
		}
	}

	@Override
	public Path fsBundlePath(final FileSystem fs, final String adaptorBundlePath) {
		return fs.getPath(adaptorBundlePath);
	}

	@Override
	public boolean isAdaptable(final FileSystem fs, final String bundlePath) {
		return locateAdaptorBundlePath(fs, bundlePath).map(fs::getPath).map(p -> p.resolve("Resources/Info.plist"))
				.map(Files::exists).orElse(false);
	}

	private Optional<String> locateAdaptorBundlePath(final FileSystem fs, final String bundlePath) {
		Optional<String> result = Optional.empty();
		Path currentPath = fs.getPath(bundlePath);
		// Trim off the Info.plist
		currentPath = currentPath.getParent();
		while (currentPath != null) {
			final Path pom = currentPath.resolve("pom.xml");
			if (Files.exists(pom)) {
				result = Optional.of(currentPath.toString());
				break;
			}
			currentPath = currentPath.getParent();
		}
		return result;
	}

	@Override
	public Properties propertiesForFileSystem(final FileSystem fs, final Path fsBundlePath) {
		// TODO Auto-generated method stub
		// not really sure what to do here
		final List<Path> resPaths = resourcePathsForFileSystem(fs, fsBundlePath);
		return new NSNestedProperties(null);
	}

	@Override
	public List<Path> resourcePathsForFileSystem(final FileSystem fs, final Path fsBundlePath) {
		return Arrays.asList("Components", "Resources", "WebServerResources").stream().map(fsBundlePath::resolve)
				.collect(Collectors.toList());
	}
}
