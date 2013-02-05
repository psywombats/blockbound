/**
 *  TextBoxMDO.java
 *  Created on Feb 2, 2013 3:35:57 AM for project RainfallSchema
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.rainfallschema.ui;

import net.wombatrpgs.mgns.core.Annotations.Desc;
import net.wombatrpgs.mgns.core.Annotations.Nullable;
import net.wombatrpgs.mgns.core.Annotations.Path;
import net.wombatrpgs.mgns.core.Annotations.SchemaLink;
import net.wombatrpgs.mgns.core.MainSchema;
import net.wombatrpgs.rainfallschema.graphics.GraphicMDO;

/**
 * Defines a textbox.
 */
@Path("ui/")
public class TextBoxMDO extends MainSchema {
	
	@Desc("Graphic - ui box thing displayed beneath the text")
	@SchemaLink(GraphicMDO.class)
	@Nullable
	public String image;
	
	@Desc("Box x - upper left x of where the text box image is displayed (in px)")
	public Integer graphicX;
	
	@Desc("Box y - upper left y of where the text box image is displayed (in px)")
	public Integer graphicY;
	
	@Desc("x1 - upper left x of the rectangle where text will be (in px)")
	public Integer x1;
	
	@Desc("y1 - upper left y of the rectangle where text will be (in px)")
	public Integer y1;
	
	@Desc("x2 - lower right x of the rectangle where text will be (in px)")
	public Integer x2;
	
	@Desc("y2 - lower right y of the rectangle where text will be (in px)")
	public Integer y2;

}