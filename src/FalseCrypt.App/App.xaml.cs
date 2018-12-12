using System.Windows;
using Caliburn.Micro;
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


            var m = new WindowManager();
            m.ShowWindow(IoC.Get<IWindowViewModel>());
        }
    }
}
