import greenfoot.*;
import java.util.*;
public class ElMacho extends Player {
    private AmmoGUI ammoGui;
    private int timeToReload;
    private boolean needToReload;
    private boolean vPressed;
    private boolean bPressed;
    private boolean rPressed;
    public boolean usedUlt;
    public int ultDur;
    public boolean isEduardo;
    private int currentFrame;
    private int frameDelay;
    private boolean isMoving;
    private Random rand;
    public ElMacho() {
        super("El Macho", 2, "macho");
        health = 700;
        hitpoints = 700;
        //ammoCount = 5;
        timeToReload = 0;

        needToReload = false;
        vPressed = false;
        bPressed = false;
        rPressed = false;
        usedUlt = false;
        isEduardo = false;
        ultDur = 0;
        rand = new Random(); 
        
        currentFrame = 0;
        frameDelay = 6;
        isMoving = false;
    }
    
    @Override
    public void addedToWorld(World world) {
        super.addedToWorld(world);
        ammoGui = new AmmoGUI(5, 5, 
            new TortillaChip(facingRight(), this).getImage(), pastHalfway);
        changePersona();
        q = new WrestlingChamp(this);
        e = new GuacamoleTortillaChip(facingRight());
        if(pastHalfway) {
            this.setImage(getLeftImage());
            getWorld().addObject(ammoGui, 1100, 400);
        }
        else {
            this.setImage(getRightImage());
            getWorld().addObject(ammoGui, 5, 400);
        }
        reload();
    }

    public void singleFire() {
        if(ammoGui.cur > 0) {
            getWorld().addObject(
                new TortillaChip(facingRight(), this), getX(), getY());
            ammoGui.loseChip();
        }
    }

    public void burstFire() {
        if(ammoGui.cur >= 3) {
            int a = 0;
            for(int k = 1; k <= 3; k++) {
                getWorld().addObject(new TortillaChip(facingRight(), this), getX() + a, getY());
                a += 60;
            }
            for(int k = 1; k <= 3; k++) {
                ammoGui.loseChip();
            }   
        }
    }

    public void reload() {
        ammoGui.refill();
        needToReload = false;
    }    

    public void timedReload() {
        if(timeToReload == 200) {
            reload();
            timeToReload = 0;
        }
        timeToReload++;
    }

    private void changePersona() {
        GreenfootImage[] tempRightFrames = new GreenfootImage[21];
        GreenfootImage[] tempLeftFrames = new GreenfootImage[21];
        GreenfootImage rightImage = new GreenfootImage(getRightImage());
        GreenfootImage leftImage = new GreenfootImage(getLeftImage());
        if(isEduardo) {
            for(int i = 0; i < tempRightFrames.length; i++) {
                tempRightFrames[i] = new GreenfootImage("eduardo" + (i + 1) + ".png");
                tempRightFrames[i].scale(tempRightFrames[i].getWidth() / 3, tempRightFrames[i].getHeight() / 3);
            }
            for(int i = 0; i < tempLeftFrames.length; i++) {
                tempLeftFrames[i] = new GreenfootImage(tempRightFrames[i]);
                tempLeftFrames[i].mirrorHorizontally();
            }
        }
        else {
            for(int i = 0; i < tempRightFrames.length; i++) {
                tempRightFrames[i] = new GreenfootImage("macho" + (i + 1) + ".png");
                tempRightFrames[i].scale(tempRightFrames[i].getWidth() / 2, tempRightFrames[i].getHeight() / 2);
            }
            for(int i = 0; i < tempLeftFrames.length; i++) {
                tempLeftFrames[i] = new GreenfootImage(tempRightFrames[i]);
                tempLeftFrames[i].mirrorHorizontally();
            }
        }
        setRightFrames(tempRightFrames);
        setLeftFrames(tempLeftFrames);
        setRightImage(tempRightFrames[0]);
        setLeftImage(tempLeftFrames[0]);
        if(facingRight()) {
            this.setImage(getRightImage());
        }
        else {
            this.setImage(getLeftImage());
        }
    }

    @Override
    public void act() {
        super.act();
        if(canCast) {
            if(ammoGui.cur <= 0 || Greenfoot.isKeyDown("R") && !rPressed) {
                if(Greenfoot.isKeyDown("R") && !rPressed) {
                    rPressed = true;
                }
                needToReload = true;
            }
            if(!Greenfoot.isKeyDown("R") && rPressed) {
                rPressed = false;
            }
            if(needToReload) {
                timedReload();
            }

            if(Greenfoot.isKeyDown("B") && !bPressed) {
                bPressed = true;
                if(needToReload) {
                    needToReload = false;
                }
                burstFire();
            }
            if(!Greenfoot.isKeyDown("B") && bPressed) {
                bPressed = false;
            } 

            if(Greenfoot.isKeyDown("V") && !vPressed) {
                vPressed = true;
                if(needToReload) {
                    needToReload = false;
                }
                singleFire();
            }
            if(!Greenfoot.isKeyDown("V") && vPressed) {
                vPressed = false;
            }    
        }
        checkAbilities();
    }    
    
    @Override
    protected void checkAbilities() {
        super.checkAbilities();
        if(q.isReady()) {
            if(isEduardo) {
                q = new Waffle(facingRight());
            }
            else {
                q = new WrestlingChamp(this);
            }
        }
        if(e.isReady()) {
            if(isEduardo) {
                e = new Sombrero();
            }
            else {
                e = new GuacamoleTortillaChip(facingRight());
            }    
        }
    }

    public void q() {
        getWorld().addObject(q, this.getX(), this.getY());
        if(!isEduardo) {
            this.setImage((GreenfootImage) null);
            this.canMove = false;
            this.canCast = false;    
        }
    }

    public void c() {
        if(isEduardo) {
            isEduardo = false;
        }
        else {
            isEduardo = true;
        }
        changePersona();
    }   

    public void e() {
        int x = this.getX();
        int y = this.getY();
        if(isEduardo) {
            x += 125;
        }
        getWorld().addObject(e, x, y);
    }

    public void x() {
        World curWorld = getWorld();
        usedUlt = true;
        canCast = false;
        List<TortillaChip> chips = new ArrayList<TortillaChip>();
        for(int k = 1; k <= 50; k++) {
            TortillaChip tc = new TortillaChip(this);
            int randX = rand.nextInt(curWorld.getWidth());
            int randY = rand.nextInt(curWorld.getHeight());
            getWorld().addObject(tc, randX, randY);
        }
    }
}