using System;
using System.Windows;
using Caliburn.Micro;
using FalseCrypt.App.ViewModels;
using FalseCrypt.Crypto;
using ModernApplicationFramework.Interfaces.Services;
using ModernApplicationFramework.Interfaces.ViewModels;
using ModernApplicationFramework.Themes;

namespace FalseCrypt.App
{
    public partial class App
    {
        private void OnStartUp(object sender, StartupEventArgs e)
        {
            IoC.Get<IThemeManager>().Theme = new BlueTheme();
            var wm = IoC.Get<IWindowManager>();
            var passwordModel = IoC.Get<EnterPasswordViewModel>();            
            var m = new WindowManager();
            m.ShowWindow(IoC.Get<IWindowViewModel>());

            wm.ShowDialog(passwordModel);
            var hash = WeakPasswordDerivation.StringToHash(passwordModel.Password);
            if (!hash.Equals(WeakCryptoConfig.password, StringComparison.InvariantCultureIgnoreCase))
                Execute.OnUIThread(() => Current.Shutdown());
        }
    }
}
