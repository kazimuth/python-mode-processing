package info.sansgills.mode.python;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import processing.app.*;
import processing.mode.java.JavaMode;
import processing.mode.java.runner.Runner;

/**
 * 
 * Python Mode. Yeah.
 * 
 */
public class PythonMode extends Mode {

	public PythonMode(Base base, File folder) {
		super(base, folder);
	}

	/**
	 * Create a new editor associated with this mode.
	 */
	public Editor createEditor(Base base, String path, EditorState state) {
		return new PythonEditor(base, path, state, this);
	}

	/**
	 * Return the pretty/printable/menu name for this mode. This is separate
	 * from the single word name of the folder that contains this mode. It could
	 * even have spaces, though that might result in sheer madness or total
	 * mayhem.
	 */
	public String getTitle() {
		return "Python";
	}

	/*
	 * Utility
	 */
	public static String getModeFolder() {
		return Base.getSketchbookModesFolder() + File.separator + "PythonMode" + File.separator;
	}

	/**
	 * Returns the default extension for this editor setup. NOTE: no '.' at the
	 * beginning, that causes problems!
	 */
	public String getDefaultExtension() {
		return "pde";
	}

	/**
	 * Returns a String[] array of proper extensions.
	 */
	public String[] getExtensions() {
		return new String[] { "pde", "py" };
	}

	/**
	 * Returns the core library; in this case, a java library, because, jython.
	 */
	@Override
	public Library getCoreLibrary() {
		if (coreLibrary == null) {
			File coreFolder = Base.getContentFile("core");
			coreLibrary = new Library(coreFolder);
		}
		return coreLibrary;
	}

	//things what come with jython
	private static String[] pythonStandardLibrary = { "string", "re", "struct", "difflib", "StringIO", "cStringIO", "textwrap", "codecs", "unicodedata", "fpformat", "datetime", "calendar", "collections", "heapq", "bisect", "array", "sets", "sched", "mutex", "Queue", "weakref", "UserDict", "UserList", "UserString", "types", "new", "copy", "pprint", "repr", "math", "cmath", "decimal", "random", "itertools", "functools", "operator", "os.path", "fileinput", "stat", "filecmp", "tempfile", "glob", "fnmatch", "linecache", "shutil", "dircache", "macpath", "zlib", "gzip", "zipfile", "json", "tarfile", "csv", "ConfigParser", "robotparser", "netrc", "xdrlib", "hashlib", "hmac", "md5", "sha", "os", "time", "optparse", "getopt", "logging", "getpass", "platform", "errno", "select", "threading", "thread", "dummy_thread", "rlcompleter", "subprocess", "socket", "ssl", "signal", "popen2", "asyncore", "asynchat", "email", "mailcap", "mailbox", "mhlib", "mimetools", "mimetypes", "MimeWriter", "mimify", "multifile", "rfc822", "base64", "binhex", "binascii", "quopri", "uu", "HTMLParser", "sgmllib", "htmllib", "htmlentitydefs", "xml.parsers.expat", "xml.dom", "xml.dom.minidom", "xml.dom.pulldom", "xml.sax", "xml.sax.handler", "webbrowser", "cgi", "cgitb", "wsgiref", "urllib", "urllib2", "httplib", "ftplib", "poplib", "imaplib", "nntplib", "smtplib", "smtpd", "telnetlib", "uuid", "urlparse", "SocketServer", "BaseHTTPServer", "SimpleHTTPServer", "CGIHTTPServer", "cookielib", "xmlrpclib", "SimpleXMLRPCServer", "DocXMLRPCServer", "aifc", "chunk", "colorsys", "imghdr", "sndhdr", "cmd", "shlex", "pydoc", "doctest", "test", "test.test_support", "bdb", "pdb", "timeit", "trace", "sys", "__builtin__", "warnings", "contextlib", "atexit", "traceback", "__future__", "gc", "inspect", "site", "user", "code", "codeop", "imp", "zipimport", "pkgutil", "modulefinder", "runpy", "symbol", "token", "keyword", "tokenize", "tabnanny", "pyclbr", "py_compile", "compileall", "dis", "pickletools", "distutils", "posix", "pwd", "grp", "pipes", "posixfile", "commands" };
	private static Set<String> pythonLibraries;
	static {
		pythonLibraries = new HashSet<String>();
		for (String lib : pythonStandardLibrary) {
			pythonLibraries.add(lib);
		}
	}

	public boolean isPythonLibrary(String lib) {
		return pythonLibraries.contains(lib);
	}

	/**
	 * Get array of file/directory names that needn't be copied during "Save
	 * As".
	 */
	public String[] getIgnorable() {
		return new String[] {};
	}

	/**
	 * Retrieve the ClassLoader for JavaMode. This is used by Compiler to load
	 * ECJ classes. Thanks to Ben Fry.
	 * 
	 * @return the class loader from java mode
	 */
	public ClassLoader getClassLoader() {
		for (Mode m : base.getModeList()) {
			if (m.getClass() == JavaMode.class) {
				JavaMode jMode = (JavaMode) m;
				return jMode.getClassLoader();
			}
		}
		return null;  // something broke
	}

}
