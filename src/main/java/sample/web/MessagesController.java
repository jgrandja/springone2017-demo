/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sample.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Joe Grandja
 */
@Controller
public class MessagesController {

	@GetMapping("create-message")
	public String createMessage() {
		return "create-message";
	}

	@GetMapping("read-message")
	public String readMessage() {
		return "read-message";
	}

	@GetMapping("update-message")
	public String updateMessage() {
		return "update-message";
	}

	@GetMapping("delete-message")
	public String deleteMessage() {
		return "delete-message";
	}

	@GetMapping("manage-message")
	public String manageMessage() {
		return "manage-message";
	}
}
