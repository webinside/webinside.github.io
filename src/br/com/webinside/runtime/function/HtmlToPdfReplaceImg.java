package br.com.webinside.runtime.function;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Element;
import org.xhtmlrenderer.extend.FSImage;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.extend.ReplacedElementFactory;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.pdf.ITextFSImage;
import org.xhtmlrenderer.pdf.ITextImageElement;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.extend.FormSubmissionListener;

import com.lowagie.text.Image;

public class HtmlToPdfReplaceImg implements ReplacedElementFactory {

	private final ReplacedElementFactory superFactory;

	public HtmlToPdfReplaceImg(ReplacedElementFactory superFactory) {
		this.superFactory = superFactory;
	}

	@Override
	public ReplacedElement createReplacedElement(LayoutContext layoutContext,
			BlockBox blockBox, UserAgentCallback userAgentCallback,
			int cssWidth, int cssHeight) {

		Element element = blockBox.getElement();
		if (element == null) return null;
		String img = element.getNodeName();
		String src = element.getAttribute("src");
		if ("img".equals(img) && src != null && src.startsWith("data:image")) {
			try {
				String base64 = src.substring(src.indexOf(",") + 1, src.length());
				byte[] bytes = Base64.decodeBase64(base64);
				Image image = Image.getInstance(bytes);
				FSImage fsImage = new ITextFSImage(image);
				if (fsImage != null) {
					if ((cssWidth != -1) || (cssHeight != -1)) {
						fsImage.scale(cssWidth, cssHeight);
					}
					return new ITextImageElement(fsImage);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return superFactory.createReplacedElement(layoutContext, blockBox,
				userAgentCallback, cssWidth, cssHeight);
	}

	@Override
	public void reset() {
		superFactory.reset();
	}

	@Override
	public void remove(Element e) {
		superFactory.remove(e);
	}

	@Override
	public void setFormSubmissionListener(FormSubmissionListener listener) {
		superFactory.setFormSubmissionListener(listener);
	}
	
}