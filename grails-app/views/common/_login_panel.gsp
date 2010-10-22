<!-- LOGIN PANEL -->
  <div id="toppanel" class="toppanel">
	<div id="panel">
		<div class="content clearfix">
			<div class="left">
				<h1>Welcome to the Metabolomics Data Support Platform</h1>
				<h2>version <b>${meta(name: 'app.version')}</b></h2>
				<p class="grey">Please use the forms on the right to either log in if you already have an account, or sign up if you think this data support platform suits your needs.</p>
				<p class="grey">Note that <span class="red">registration</span> and <span class="red">password reminders</span> are not yet implemented and hence <span class="red">do not work</span> at this moment!</p>
				<g:if test="${flash.message}"><p class="red">${flash.message}</p></g:if>
			</div>
			<div class="left">
				<g:form controller="." action="j_spring_security_check" method='POST' class="clearfix">
					<h1>Member Login</h1>
					<label class="grey" for="username">Username:</label>
					<input class="field" type="text" name="j_username" id="j_username" value="${username}" size="23" />
					<label class="grey" for="password">Password:</label>
					<input class="field" type="password" name="j_password" id="password" size="23" />
					<label><input type='checkbox' class='chk' name='_spring_security_remember_me' id='remember_me'
					<g:if test='${hasCookie}'>checked='checked'</g:if> /> Remember me</label>
                                        <div class="clear"></div>
					<input type="submit" name="submit" value="Login" class="bt_login" />

					<g:if test="${redirectUrl}">
					  <g:hiddenField name="spring-security-redirect" value="${redirectUrl}" />
					</g:if>

					<a class="lost-pwd" href="<g:createLink url="[action:'forgotPassword',controller:'register']" class="lost-pwd" />">Lost your password?</a>
				</g:form>
			</div>
			<div class="left right">
                          <g:form url="[action:'add',controller:'userRegistration']" class="clearfix">
					<input type="hidden" name="targetUri" value="${targetUri}" />
					<h1>Not a member yet? Sign Up!</h1>
					<label class="grey" for="signup">Username:</label>
					<input class="field" type="text" name="username" id="username" value="${username}" size="23" />
					<label class="grey" for="email">Email:</label>
					<input class="field" type="text" name="email" id="email" value="${email}" size="23" />
                                        <label>A password will be e-mailed to you</label>

					<input type="submit" name="submit" value="Register" class="bt_register" />
                            </g:form>
			</div>
		</div>
	</div>
	<div class="tab">
		<ul class="login">
			<li class="left">&nbsp;</li>
			<li>Hello <sec:ifLoggedIn><sec:username/></sec:ifLoggedIn>
                        <sec:ifNotLoggedIn>Guest</sec:ifNotLoggedIn>!</li>
			<li class="sep">|</li>
			<li id="toggle">
                        <sec:ifLoggedIn><g:link controller="logout" action="index">sign out</g:link></sec:ifLoggedIn>
                            <sec:ifNotLoggedIn>
                             <a id="open" class="open" href="#">Log In | Register</a>
                             <a id="close" style="display: none;" class="close" href="#">Close Panel</a>
                            </sec:ifNotLoggedIn>
			</li>
	    <li class="right">&nbsp;</li>
		</ul>
	</div>
  </div>
<!-- /LOGIN PANEL -->