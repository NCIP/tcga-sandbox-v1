package com.fusioncharts.jsp.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * Tag to enable the FCPrintManager. Set enabled attribute value to "true".
 * (default)
 * 
 * @author InfoSoft Global (P) Ltd.
 * 
 */
public class PrintManagerTag extends BodyTagSupport {

	private static final long serialVersionUID = 2L;

	protected Boolean enabled = new Boolean(true);

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doEndTag()
	 */
	@Override
	public int doEndTag() throws JspException {

		return SKIP_BODY;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doStartTag()
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			JspWriter writer = pageContext.getOut();
			if (enabled) {
				writer
						.write("<script type='text/javascript'><!--\n FusionCharts.printManager.enabled("
								+ enabled.booleanValue()
								+ ");\n// -->\n</script>");
			}
		} catch (Exception ex) {
			throw new JspTagException("PrintManagerTag: " + ex.getMessage());
		}
		return SKIP_BODY;
	}

	/**
	 * Returns the value in the field enabled
	 * 
	 * @return the enabled
	 */
	public Boolean getEnabled() {
		return enabled;
	}

	/**
	 * Release all members.
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#release()
	 */
	@Override
	public void release() {
		// the super class method should be called
		super.release();
		enabled = null;
	}

	/**
	 * Sets the value for enabled
	 * 
	 * @param enabled
	 *            the enabled to set
	 */
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

}
