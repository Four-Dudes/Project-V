import greenfoot.*;
public class TortillaChip extends Weapon {
    private static int damage = 10;
    public static GreenfootImage img;
    public static GreenfootImage left;
    private boolean right;
    public TortillaChip(boolean right) {
        super(new ElMacho());
        this.right = right;
        img = this.getImage();
        img.scale(img.getWidth() / 7, img.getHeight() / 7);
        img.mirrorHorizontally();
        left = new GreenfootImage(img);
        left.mirrorHorizontally();
        setImage(img);
        if(!right) {
            getImage().mirrorHorizontally();
        }
    }

    public TortillaChip() {
        super(new ElMacho());
        img = this.getImage();
        img.scale(img.getWidth() / 7, img.getHeight() / 7);
        img.mirrorHorizontally();
        setImage(img);
    }

    public void act() {
        if(right) {
            this.setLocation(this.getX() + 8, this.getY());
        }
        else{
            this.setLocation(this.getX() - 8, this.getY());
        }
        detectCollision("ElMacho", damage);
    }
}