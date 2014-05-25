/*
 * Copyright 2008 (C) Chunyun Zhao(Chunyun.Zhao@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.jolm.maven.mojo;

import org.apache.maven.plugin.logging.Log;

/**
 * @author Chunyun Zhao
 */
public class Logger {
	private static Logger instance = new Logger();
	private boolean verbose;
	private Log log;
	
	public Log getLog() {
		return log;
	}

	public void setLog(Log log) {
		this.log = log;
	}

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public static Logger getInstance() {
		return instance;
	}

	public void debug(CharSequence content, Throwable error) {
		if ( verbose ) {
			log.info(content, error);
		}
	}

	public void debug(CharSequence content) {
		if ( verbose ) {
			log.info(content);
		}
	}

	public void debug(Throwable error) {
		if ( verbose ) {
			log.info(error);
		}
	}


	public void info(CharSequence content, Throwable error) {
		log.info(content, error);
	}

	public void info(CharSequence content) {
		log.info(content);
	}

	public void info(Throwable error) {
		log.info(error);
	}

	public void error(CharSequence content, Throwable error) {
		log.error(content, error);
	}

	public void error(CharSequence content) {
		log.error(content);
	}

	public void error(Throwable error) {
		log.error(error);
	}

	public void warn(CharSequence content, Throwable error) {
		log.warn(content, error);
	}

	public void warn(CharSequence content) {
		log.warn(content);
	}

	public void warn(Throwable error) {
		log.warn(error);
	}
}
