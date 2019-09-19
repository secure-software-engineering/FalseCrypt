using Prism.Commands;
using System;
using System.Windows.Input;

namespace FalseCrypt.App.Command
{
    public abstract class AbstractCommandWrapper : ICommand
    {

        protected AbstractCommandWrapper()
        {
            
        }

        protected AbstractCommandWrapper(Action executeAction, Func<bool> cantExectueFunc)
        {
            if (executeAction == null)
                throw new ArgumentNullException(nameof(executeAction));
            if (cantExectueFunc == null)
                throw new ArgumentNullException(nameof(cantExectueFunc));
            WrappedCommand = new DelegateCommand(executeAction, cantExectueFunc);
        }

        protected AbstractCommandWrapper(ICommand wrappedCommand)
        {
            WrappedCommand = wrappedCommand ?? throw new ArgumentNullException(nameof(wrappedCommand));
        }

        public ICommand WrappedCommand { get; protected internal set; }

        public bool CanExecute(object parameter)
        {
            if (WrappedCommand == null)
                return false;
            return WrappedCommand.CanExecute(parameter);
        }

        public event EventHandler CanExecuteChanged
        {
            add => CommandManager.RequerySuggested += value;
            remove => CommandManager.RequerySuggested -= value;
        }

        public void Execute(object parameter)
        {
            WrappedCommand?.Execute(parameter);
        }
    }
}
