package me.hgj.jetpackmvvm.demo.ui.fragment.publicNumber

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.kingja.loadsir.core.LoadService
import kotlinx.android.synthetic.main.include_viewpager.*
import me.hgj.jetpackmvvm.demo.R
import me.hgj.jetpackmvvm.demo.app.base.BaseFragment
import me.hgj.jetpackmvvm.demo.app.ext.*
import me.hgj.jetpackmvvm.demo.app.weight.loadCallBack.LoadingCallback
import me.hgj.jetpackmvvm.demo.app.weight.loadCallBack.ErrorCallback
import me.hgj.jetpackmvvm.demo.data.model.bean.ClassifyResponse
import me.hgj.jetpackmvvm.demo.databinding.FragmentViewpagerBinding
import me.hgj.jetpackmvvm.demo.viewmodel.request.PublicNumberViewModel
import me.hgj.jetpackmvvm.ext.parseState

/**
 * 作者　: hegaojian
 * 时间　: 2019/12/28
 * 描述　:聚合公众号
 */
class PublicNumberFragment : BaseFragment<PublicNumberViewModel, FragmentViewpagerBinding>() {

    //界面状态管理者
    private lateinit var loadsir: LoadService<Any>

    //fragment集合
    private var fragments: ArrayList<Fragment> = arrayListOf()

    //标题集合
    private var mDataList: ArrayList<ClassifyResponse> = arrayListOf()

    override fun layoutId() = R.layout.fragment_viewpager

    override fun initView(savedInstanceState: Bundle?)  {
        //状态页配置
        loadsir = LoadServiceInit(view_pager) {
            //点击重试时触发的操作
            loadsir.showCallback(LoadingCallback::class.java)
            mViewModel.getPublicTitleData()
        }
        //初始化viewpager2
        view_pager.init(this,fragments)
        //初始化 magic_indicator
        magic_indicator.bindViewPager2(view_pager,mDataList)
        //初始化时设置顶部主题颜色
        shareViewModel.appColor.value.let { viewpager_linear.setBackgroundColor(it) }
    }

    /**
     * 懒加载
     */
    override fun lazyLoadData() {
        //请求标题数据
        mViewModel.getPublicTitleData()
    }

    override fun createObserver() {
        mViewModel.titleData.observe(viewLifecycleOwner, Observer { data ->
            parseState(data, {
                mDataList.addAll(it)
                it.forEach { classify ->
                    fragments.add(PublicChildFragment.newInstance(classify.id))
                }
                magic_indicator.navigator.notifyDataSetChanged()
                view_pager.adapter?.notifyDataSetChanged()
                view_pager.offscreenPageLimit = fragments.size
                loadsir.showSuccess()
            }, {
                //请求项目标题失败
                loadsir.showCallback(ErrorCallback::class.java)
                loadsir.setErrorText(it.errorMsg)
            })
        })
        shareViewModel.appColor.observe(viewLifecycleOwner, Observer {
            setUiTheme(it, viewpager_linear,loadsir)
        })
    }
}