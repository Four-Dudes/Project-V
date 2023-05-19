import greenfoot.*;
import java.util.*;
public abstract class CPU extends Actor {
    private int playerScore;

    protected boolean isFacingRight;
    protected boolean canMove;
    protected boolean canCast;

    private Ability c;
    private Ability q;
    private Ability e;
    private UltimateAbility x;
    private Ability b;
    private Ability v;
    
    private Player human;

    private HealthBar hp;

    private int health;
    private int hitpoints;

    protected boolean pastHalfway;

    private GreenfootImage[] rightFrames;
    private GreenfootImage[] leftFrames;
    private GreenfootImage right;
    private GreenfootImage left;
    private int factor;
    private int currentFrame;
    private int frameDelay;
    private int frameDelayCopy;
    private boolean isMoving;
    private int timeDisabled;
    private boolean cPressed=false;
    private boolean bPressed=false;
    
    private String name;    
    private String nickname;
    public CPU(String name, int factor, 
    boolean isFacingRight, int health, int hitpoints) { 
        this.isFacingRight = isFacingRight;
        pastHalfway = false;
        this.health = health;
        this.hitpoints = hitpoints;
        this.name = name;
        this.nickname = nickname;
        this.playerScore = 0;
        hp = new HealthBar(name);
        this.factor = factor;
        this.canMove = true;
        this.canCast = true;
        timeDisabled = 0;
        right = getImage();
        right.scale(right.getWidth() / factor, right.getHeight() / factor);
        left = new GreenfootImage(right);
        left.mirrorHorizontally();
        if(facingRight()) {
            setImage(right);
        }
        else {
            setImage(left);
        }
    }

    public CPU(String name, int factor, boolean isFacingRight, 
    int health, int hitpoints, String nickname, int frameDelay) {
        this(name, factor, isFacingRight, health, hitpoints);
        this.human=human;
        this.rightFrames = new GreenfootImage[21];
        this.leftFrames = new GreenfootImage[21];
        for(int i = 0; i < rightFrames.length; i++) {
            if(facingRight()) {
                rightFrames[i] = new GreenfootImage(nickname + (i + 1) + ".png");
                rightFrames[i].scale(rightFrames[i].getWidth() / factor, 
                    rightFrames[i].getHeight() / factor); 
                leftFrames[i] = new GreenfootImage(rightFrames[i]);
                leftFrames[i].mirrorHorizontally();
            }
            else {
                leftFrames[i] = new GreenfootImage(nickname + (i + 1) + ".png");
                leftFrames[i].scale(leftFrames[i].getWidth() / factor, 
                    leftFrames[i].getHeight() / factor); 
                rightFrames[i] = new GreenfootImage(leftFrames[i]);
                rightFrames[i].mirrorHorizontally();
            }
        }
        this.frameDelay = frameDelay;
        frameDelayCopy = frameDelay;
        currentFrame = 0;
        isMoving = false;
        right = new GreenfootImage(rightFrames[0]);
        left = new GreenfootImage(leftFrames[0]);
        if(facingRight()) {
            setImage(right);
        }
        else {
            setImage(left);
        }
    }

    public CPU(String name, int factor, boolean isFacingRight, int health, int hitpoints, 
    String nickname, int frameDelay, Ability c, Ability q, Ability e, 
    UltimateAbility x, Ability b, Ability v) {
        this(name, factor, isFacingRight, health, hitpoints, nickname, frameDelay);
        this.c = c;
        this.q = q;
        this.e = e;
        this.x = x;
        this.b = b;
        this.v = v;
    }

    public void setFrameDelay(int frameDelay) {
        this.frameDelay = frameDelay;
    }

    public boolean facingRight() {
        return isFacingRight;
    }

    public int getHealth() {
        return health;
    }

    public void setRightFrames(GreenfootImage[] rightFrames) {
        this.rightFrames = rightFrames;
    }

    public void setLeftFrames(GreenfootImage[] leftFrames) {
        this.leftFrames = leftFrames;
    }

    public GreenfootImage[] getLeftFrames() {
        return leftFrames;
    }

    public GreenfootImage[] getRightFrames() {
        return rightFrames;
    }

    public final void decreaseHealth(int damage) {
        health -= damage;
        GreenfootImage img = hp.getImage();
        double percentage = (double) health / hitpoints;
        int width = (int) (percentage * img.getWidth());
        img.setColor(Color.RED);
        img.fillRect(0, 0, img.getWidth(), img.getHeight());
        img.setColor(Color.GREEN);
        img.fillRect(0, 0, width, img.getHeight());
        hp.drawHeader();
        hp.setImage(img);
    }

    @Override
    public void addedToWorld(World world) {
        updatePosition();
        world.addObject(this.getHealthBar(), this.getX(), 50);
    }

    public HealthBar getHealthBar() {
        return hp;
    }

    public void setRightImage(GreenfootImage right) {
        this.right = right;
    }

    public void setLeftImage(GreenfootImage left) {
        this.left = left;
    }

    public boolean isDead() {
        if(health <= 0) {
            canMove = false;
            return true;
        }
        return false;
    }

    public Ability getCAbility() {
        return c;
    }

    public Ability getQAbility() {
        return q;
    }

    public Ability getEAbility() {
        return e;
    }

    public UltimateAbility getUltimateAbility() {
        return x;
    }

    public Ability getBAbility() {
        return b;
    }

    public Ability getVAbility() {
        return v;
    }

    public void setCAbility(Ability c) {
        this.c = c;
    }

    public void setQAbility(Ability q) {
        this.q = q;
    }

    public void setEAbility(Ability e) {
        this.e = e;
    }

    public void setXAbility(UltimateAbility x) {
        this.x = x;
    }

    public void setBAbility(Ability b) {
        this.b = b;
    }

    public void setVAbility(Ability v) {
        this.v = v;
    }

    public void act() {
        if(!this.isDead()) {
            if(getTimeDisabled() == 100 && !canMove && this.getRotation() == 90) {
                this.setRotation(0);
                canMove = true;
                setTimeDisabled(0);
            }
            if(canMove) {
                move();
                checkEdges();    
            }
        }
        if(!canMove) {
            timeDisabled += 1;
        }
        updateAbility(c);
        updateAbility(q);
        updateAbility(e);
        updateAbility(b);
        checkAbilities();
    }

    public void scaleImage(GreenfootImage img) {
        img.scale(img.getWidth() / factor, img.getHeight() / factor);
    }

    public int getTimeDisabled() {
        return timeDisabled;
    }

    public void setTimeDisabled(int timeDisabled) {
        this.timeDisabled = timeDisabled;
    }

    public final void checkEdges() {
        if(this.isAtEdge()) {
            if(this.getX() == 0) {
                this.setLocation(getWorld().getWidth() - 1, getY());
            }
            else if(this.getX() == getWorld().getWidth() - 1) {
                this.setLocation(0, getY());
            }
        }
    }

    public final void move() {
        if(Greenfoot.isKeyDown("A")) {
            this.setLocation(this.getX() - 5, this.getY());
            isFacingRight = false;
            isMoving = true;
        }
        if(Greenfoot.isKeyDown("D")) {
            this.setLocation(this.getX() + 5, this.getY());
            isFacingRight = true;
            isMoving = true;
        }
        if(!Greenfoot.isKeyDown("A") && !Greenfoot.isKeyDown("D") ||
        Greenfoot.isKeyDown("A") && Greenfoot.isKeyDown("D")) {
            isMoving = false;
        }
        if(isMoving) {
            animate();
        }
        else {
            if(facingRight()) {
                this.setImage(right);
            }
            else {
                this.setImage(left);
            }
        }
    }

    public final void animate() {
        if(isFacingRight) {
            this.setImage(rightFrames[currentFrame]);   
        }
        else {
            this.setImage(leftFrames[currentFrame]);
        }
        if(frameDelay == 0) {
            currentFrame++;
            frameDelay = frameDelayCopy;
        }
        frameDelay--;
        if(currentFrame == 21) {
            currentFrame = 0;
        }
    }

    private void updateAbility(Ability ab) {
        if(ab != null && ab.isFinished()) {
            canCast = true;
            canMove = true;
        }
    }

    protected void checkAbilities() {
        if(q != null) {
            if(q.isReady()) {
                
            }
            int qCharge = q.getCharge();
            if(qCharge < q.getCooldown()) {
                qCharge--;
                q.setCharge(qCharge);
            }
            if(q.getCharge() == 0) {
                q.setCharge(q.getCooldown());
            }    
        }
        if(e != null) {
            int eCharge = e.getCharge();
            if(eCharge < e.getCooldown()) {
                eCharge--;
                e.setCharge(eCharge);
            }
        }
        if(c != null) {
            if(c.isReady()) {
                
            }
            int cCharge = c.getCharge();
            if(cCharge < c.getCooldown()) {
                cCharge--;
                c.setCharge(cCharge);
            }
            if(c.getCharge() == 0) {
                cPressed = false;
                c.setCharge(c.getCooldown());
            }
        }
        if(b != null) {
            if(b.isReady()) {
                
            }
            int bCharge = b.getCharge();
            if(bCharge < b.getCooldown()) {
                bCharge--;
                b.setCharge(bCharge);
            }
            if(b.getCharge() == 0) {
                bPressed = false;
                b.setCharge(b.getCooldown());
            }
        }
    }

    private void updatePosition() {
        if(this.getX() >= 600) {
            pastHalfway = true;
        }
        else {
            pastHalfway = false;
        }
        if(pastHalfway) {
            this.setImage(left);
            isFacingRight = false;
        }    
        else {
            this.setImage(right);
            isFacingRight = true;
        }
    }

    public int getPlayerScore() {
        return playerScore;
    }

    public GreenfootImage getRightImage() {
        return right;
    }

    public GreenfootImage getLeftImage() {
        return left;
    }

    public void reload() {
        //empty method body, but not every class needs to override it
    }

    public abstract void singleFire();

    public abstract void burstFire();

    public abstract void c();

    public abstract void q();

    public abstract void e();

    public abstract void x();
}