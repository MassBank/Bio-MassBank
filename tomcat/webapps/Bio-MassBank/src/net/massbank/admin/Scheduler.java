/*******************************************************************************
 *
 * Copyright (C) 2014 MassBank Project
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *******************************************************************************
 *
 * Scheduler.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.admin;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;


public class Scheduler extends HttpServlet {

	private static final int MAX_THREAD_POOL_SIZE = 5;
	private ScheduledExecutorService sc = null;
	private ScheduledFuture<?>[] futures = null;

	/**
	 *
	 */
	public void init() throws ServletException {
		sc = Executors.newScheduledThreadPool(MAX_THREAD_POOL_SIZE);
		futures = new ScheduledFuture<?>[MAX_THREAD_POOL_SIZE];
		futures[0] = sc.scheduleWithFixedDelay(new CreateStructureImage(), 1, 60, TimeUnit.MINUTES);
	}

	/**
	 *
	 */
	public void destroy() {
		if ( futures != null ) {
			for ( ScheduledFuture<?> future : futures ) {
				if ( future != null ) {
					future.cancel(true);
				}
			}
		}
		if ( sc != null ) {
			sc.shutdown();
		}
	}
}
