using System;
using System.Windows.Input;

namespace FalseCrypt.App.Command
{
    public class UICommand : AbstractCommandWrapper
    {
        public UICommand(Action executeAction, Func<bool> cantExectueFunc) : base(executeAction, cantExectueFunc)
        {
        }

        public UICommand(ICommand wrappedCommand) : base(wrappedCommand)
        {
        }
    }
}
