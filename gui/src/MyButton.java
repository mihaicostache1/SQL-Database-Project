import javax.swing.*;
import java.awt.*;

public class MyButton extends JButton
{
    public MyButton(String s)
    {
        super(s);
        setPreferredSize(new Dimension(300, 120));
        setFont(new Font("Calibri", Font.BOLD, 30));
    }
}
