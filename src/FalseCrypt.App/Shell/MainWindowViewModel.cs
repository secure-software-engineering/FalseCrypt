using System.ComponentModel.Composition;
using Caliburn.Micro;
using FalseCrypt.App.ViewModels;
using ModernApplicationFramework.Basics.WindowModels;
using ModernApplicationFramework.Interfaces.ViewModels;

namespace FalseCrypt.App.Shell
{
    [Export(typeof(IWindowViewModel))]
    public class MainWindowViewModel : MainWindowViewModelConductor
    {
        protected override void OnViewReady(object view)
        {
            base.OnViewReady(view);
            UseTitleBar = true;
            UseMenu = true;
        }

        public override void OnImportsSatisfied()
        {
            base.OnImportsSatisfied();
            ActivateItem(IoC.Get<TestScreenViewModel>());
        }
    }
}
