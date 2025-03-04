<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Reset Password</title>
</head>
<body>
<!-- [if !mso]><!--><!--![endif]--><!-- Normalize Styles --><!-- [if gte mso 9]>
<style type="text/css">
            /* What it does: Normalize space between bullets and text. */
            /* https://litmus.com/community/discussions/1093-bulletproof-lists-using-ul-and-li */
            li {
                text-indent: -1em;
            }
</style>
<![endif]--><!-- Progressive Enhancements -->
<div style="display: none; font-size: 1px; line-height: 1px; max-height: 0; max-width: 0; opacity: 0; overflow: hidden; mso-hide: all; font-family: sans-serif;">
    &nbsp;
</div>
 <table style="background: #F7F8FA; border: 0; border-radius: 0; width: 100%;" cellspacing="0" cellpadding="0">
  <tbody>
  <tr>
	<td>
	 <#include "header.ftlh" />
	</td>
  </tr>
  <tr>
  <td class="tw-body" style="padding: 15px 15px 0;" align="center">
	<table style="background: #F7F8FA; border: 0; border-radius: 0;" cellspacing="0" cellpadding="0">
		<tbody>
		<tr>
		 <td class="" style="width: 700px;" align="center">
			<p style="padding: 5px 5px 5px; font-size: 13px; margin: 0 0 0px; color: #316fea;" align="right">
			</p>
			<table style="background: #ffffff; border: 0px; border-radius: 4px; width: 99.6672%; overflow: hidden;"
				   cellspacing="0" cellpadding="0">
			 <tbody>
			 <tr>
				<td class="" style="padding: 0px; width: 100%;" align="center">
					
				<table dir="ltr" style="border: 0; width: 100%;" cellspacing="0" cellpadding="0">
					<tbody>
					<tr>
					 <td class="tw-card-body"
						style="padding: 20px 35px; text-align: left; color: #6f6f6f; font-family: sans-serif; border-top: 0;">
						<h1 class="tw-h1"
							style="font-size: 24px; font-weight: bold; mso-line-height-rule: exactly; line-height: 32px; margin: 0 0 20px; color: #474747;">
							Hello ${username},</h1>
						<p class="" style="margin: 20px 0; font-size: 16px; mso-line-height-rule: exactly; line-height: 24px;">
						<span style="font-weight: 400;">Please click the below button to change your password for <strong>Renter Score Application</strong>.The link will be valid for next ${expirationTime} minutes.</span><br/><br/><span
							style="font-weight: 400;">
						<table style="border: 0; width: 100%;" cellspacing="0" cellpadding="0">
							<tbody>
							<tr>
							 <td>
							 <table class="button mobile-w-full"
								   style="border: 0px; border-radius: 7px; margin: 0px auto; width: 525px; background-color: #198754; height: 50px;"
								   cellspacing="0" cellpadding="0" align="center">
								<tbody>
								<tr>
								 <td class="button__td "
									style="border-radius: 7px; text-align: center; width: 523px;"><!-- [if mso]>
									<a href="" class="button__a" target="_blank"
									   style="border-radius: 4px; color: #FFFFFF; display: block; font-family: sans-serif; font-size: 18px; font-weight: bold; mso-height-rule: exactly; line-height: 1.1; padding: 14px 18px; text-decoration: none; text-transform: none; border: 1px solid #316FEA;"> </a>
									<![endif]--> <!-- [if !mso]><!-->
									<a
											class="button__a"
											style="border-radius: 4px; color: #ffffff; display: block; font-family: sans-serif; font-size: 18px; font-weight: bold; mso-height-rule: exactly; line-height: 1.1; padding: 14px 18px; text-decoration: none; text-transform: none; border: 0;"
											href="${userResetPasswordLink}"
											target="_blank"
											rel="noopener">Reset Link</a> <!--![endif]-->
											</td>
								</tr>
								</tbody>
							 </table>
							 </td>
						    </tr>
						    </tbody>
					    </table>
                                           
						<p class="" style="margin: 20px 0; font-size: 16px; mso-line-height-rule: exactly; line-height: 24px;">
							Contact our support team if you have any questions or concerns.&nbsp;<a
								style="color: #316fea; text-decoration: none;"
								href="web-page-link" target="_blank" rel="noopener">Ask us any
							question</a></p>
						<p class="tw-signoff"
						   style="margin: 45px 0 5px; font-size: 16px; mso-line-height-rule: exactly; line-height: 24px;">
							Our best, <br/> Renter Score IT Team</p>
					 </td>
					</tr>
					</tbody>
				</table>
			 </td>
		     </tr>
		     </tbody>
	        </table>
		  </td>
		</tr>
	  </tbody>
	</table>
  </td>
   </tr>
   <tr>
		<td>
		<p>
		 <#include "footer.ftlh" />
		</p>
		</td>
		</tr>
   </tbody>
  </table>
</body>
</html>