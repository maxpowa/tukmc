package maxpowa.tukmc;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.settings.EnumOptions;
import net.minecraft.util.StringTranslate;

import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiMenuSlider extends GuiButton
{
    /** The value of this slider control. */
    public float sliderValue = 1.0F;

    /** Is this slider control being dragged. */
    public boolean dragging = false;
    
    private int id;

    public GuiMenuSlider(int ident, int x, int y, String text, float size)
    {
        super(ident, x, y, 150, 20, text);
        id = ident;
        this.sliderValue = size;
    }

    /**
     * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this button and 2 if it IS hovering over
     * this button.
     */
    protected int getHoverState(boolean par1)
    {
        return 0;
    }

    /**
     * Fired when the mouse button is dragged. Equivalent of MouseListener.mouseDragged(MouseEvent e).
     */
    protected void mouseDragged(Minecraft par1Minecraft, int par2, int par3)
    {
        if (this.drawButton)
        {
            if (this.dragging)
            {
                this.sliderValue = (float)(par2 - (this.xPosition + 4)) / (float)(this.width - 8);

                if (this.sliderValue < 0.0F)
                {
                    this.sliderValue = 0.0F;
                }

                if (this.sliderValue > 1.0F)
                {
                    this.sliderValue = 1.0F;
                }

                switch (id) {
    	            case 0: {
    	            	TukMCReference.RED = Math.round(this.sliderValue*255);
    		    		break;
    	            }
    	            case 1: {
    	            	TukMCReference.GREEN = Math.round(this.sliderValue*255);
    		    		break;
    	            }
    	            case 2: {
    	            	TukMCReference.BLUE = Math.round(this.sliderValue*255);
    		    		break;
    	            }
                	default: {
        	    		break;
                	}
                }
                this.displayString = getKeyBinding(id);

                
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexturedModalRect(this.xPosition + (int)(this.sliderValue * (float)(this.width - 8)), this.yPosition, 0, 66, 4, 20);
            this.drawTexturedModalRect(this.xPosition + (int)(this.sliderValue * (float)(this.width - 8)) + 4, this.yPosition, 196, 66, 4, 20);
        }
    }

    /**
     * Returns true if the mouse has been pressed on this control. Equivalent of MouseListener.mousePressed(MouseEvent
     * e).
     */
    public boolean mousePressed(Minecraft par1Minecraft, int par2, int par3)
    {
        if (super.mousePressed(par1Minecraft, par2, par3))
        {
            this.sliderValue = (float)(par2 - (this.xPosition + 4)) / (float)(this.width - 8);

            if (this.sliderValue < 0.0F)
            {
                this.sliderValue = 0.0F;
            }

            if (this.sliderValue > 1.0F)
            {
                this.sliderValue = 1.0F;
            }

            switch (id) {
	            case 0: {
	            	TukMCReference.RED = Math.round(this.sliderValue*255);
		    		break;
	            }
	            case 1: {
	            	TukMCReference.GREEN = Math.round(this.sliderValue*255);
		    		break;
	            }
	            case 2: {
	            	TukMCReference.BLUE = Math.round(this.sliderValue*255);
		    		break;
	            }
            	default: {
    	    		break;
            	}
            }
            this.displayString = getKeyBinding(id);
            this.dragging = true;
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public void refresh() {
        this.displayString = getKeyBinding(id);
    }
    
    /**
     * Gets a key binding.
     */
    public String getKeyBinding(int id)
    {
    	String returnstr;
    	switch (id) {
	    	case 0 :
	    		returnstr = "RED : " + TukMCReference.RED;
	    		break;
	    	case 1 :
	    		returnstr = "GREEN : " + TukMCReference.GREEN;
	    		break;
	    	case 2 :
	    		returnstr = "BLUE : " + TukMCReference.BLUE ;
	    		break;
	    	default :
	    		returnstr = "This shouldn't exist.";
	    		break;
    	}
		return returnstr;
    }

    /**
     * Fired when the mouse button is released. Equivalent of MouseListener.mouseReleased(MouseEvent e).
     */
    public void mouseReleased(int par1, int par2)
    {
        this.dragging = false;
    }
}
