package com.surendramaran.yolov9tflite

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.Storage
import io.ktor.http.ContentType
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import com.bumptech.glide.load.resource.bitmap.CircleCrop
class Home : AppCompatActivity() {
    private val supabase by lazy {
        createSupabaseClient(
            supabaseUrl = "https://ydavhbhglxgzeiggzpgg.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InlkYXZoYmhnbHhnemVpZ2d6cGdnIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDM4MjQ4NjEsImV4cCI6MjA1OTQwMDg2MX0.7az8QQl04TZigqDsz5RPmTzeGGG_4TRc1QU-ROKTiPc"
        ) {
            install(Postgrest)
            install(Storage)
        }
    }
    lateinit var userId: String
    lateinit var postId: String
    private lateinit var rvPost: RecyclerView
    private lateinit var avatarImageView: ImageView
    private lateinit var username: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }


        //Anh xa
        avatarImageView = findViewById(R.id.imageAvatar)
        username = findViewById(R.id.textName)
        rvPost = findViewById(R.id.rv_post)
        rvPost.layoutManager = GridLayoutManager(this, 2)

// Khởi tạo adapter với callback khi click vào item
        val adapter = PostAdapter(mutableListOf()) { selectedPost ->
            val intent = Intent(this, ChiTietBaiViet::class.java)

            intent.putExtra("title", selectedPost.title)
            intent.putExtra("content", selectedPost.content)
            intent.putExtra("imageUrl", selectedPost.imageUrl)
            intent.putExtra("date", selectedPost.date)
            intent.putExtra("userId", userId)
            intent.putExtra("postId", selectedPost.id)
            startActivity(intent)
        }

        rvPost.adapter = adapter


        fetchPosts { postList ->
            adapter.updateData(postList)
        }



        //nhan data
        val email = intent.getStringExtra("email")

        username.setText(email)
        // Dùng email này để truy vấn ảnh người dùng trên Supabase
        if (email != null) {
//            Toast.makeText(this, "Đã nhận được email: $email", Toast.LENGTH_SHORT).show()
            fetchUserAvatarByEmail(email)
        } else {
            Toast.makeText(this, "Không nhận được email", Toast.LENGTH_SHORT).show()
        }

        val danhSachConTrung = listOf(
            ConTrung(
                id = 1,
                tenTiengViet = "Bọ sọc dưa",
                tenKhoaHoc = "Acalymma vittatum",
                loai = "Gây hại",
                moTa = "Gây hại chủ yếu trên cây họ bầu bí, ăn lá và truyền bệnh héo vi khuẩn.",
                phanBo = "Phân bố rộng rãi ở Bắc Mỹ, đặc biệt là vùng ôn đới.",
                hinhThai = "Cơ thể màu vàng nhạt với ba sọc đen chạy dọc lưng, dài khoảng 5-7mm.",
                phongTru = "Luân canh cây trồng, sử dụng lưới che, bẫy màu vàng, và thuốc trừ sâu sinh học."

            ),
            ConTrung(
                id = 2,
                tenTiengViet = "Bọ nhảy",
                tenKhoaHoc = "Alticini",
                loai = "Gây hại",
                moTa = "Là nhóm bọ nhỏ có khả năng nhảy xa, gây hại cây non bằng cách đục lá.",
                phanBo = "Phân bố toàn cầu, phổ biến ở khu vực nhiệt đới và cận nhiệt đới.",
                hinhThai = "Kích thước nhỏ (1-3mm), màu sắc đa dạng, chân sau phát triển mạnh để nhảy.",
                phongTru = "Che phủ đất, sử dụng thuốc trừ sâu sinh học, giữ độ ẩm đất để hạn chế hoạt động."
            ),
            ConTrung(
                id = 3,
                tenTiengViet = "Bọ xít bí",
                tenKhoaHoc = "Anasa tristis (Squash Bug)",
                loai = "Gây hại",
                moTa = "Hút nhựa cây bầu bí làm cây héo và suy yếu, có thể gây chết cây.",
                phanBo = "Bắc Mỹ và Trung Mỹ.",
                hinhThai = "Cơ thể dài khoảng 15mm, màu xám nâu, có mùi hôi khi bị đe dọa.",
                phongTru = "Loại bỏ trứng, dùng vải phủ cây, sử dụng thiên địch như ong ký sinh Trichopoda pennipes."
            ),
            ConTrung(
                id = 4,
                tenTiengViet = "Bọ măng tây",
                tenKhoaHoc = "Crioceris asparagi",
                loai = "Gây hại",
                moTa = "Chuyên gây hại cây măng tây, ăn lá và ngọn non.",
                phanBo = "Châu Âu và Bắc Mỹ.",
                hinhThai = "Cơ thể thon dài, màu cam hoặc đỏ với các đốm đen.",
                phongTru = "Thu gom và tiêu diệt bọ bằng tay, dùng neem oil hoặc thuốc trừ sâu sinh học."
            ),
            ConTrung(
                id = 5,
                tenTiengViet = "Bọ bí",
                tenKhoaHoc = "Aulacophora femoralis",
                loai = "Gây hại",
                moTa = "Phá hoại lá cây họ bầu bí, ăn lá làm giảm khả năng quang hợp.",
                phanBo = "Châu Á, đặc biệt phổ biến ở Đông Nam Á.",
                hinhThai = "Cánh cứng màu cam đỏ, cơ thể nhỏ, dài khoảng 6-8mm.",
                phongTru = "Sử dụng bẫy màu, thiên địch hoặc phun thuốc thảo mộc."
            ),
            ConTrung(
                id = 6,
                tenTiengViet = "Bọ tai kẹp",
                tenKhoaHoc = "Dermaptera",
                loai = "Không gây hại",
                moTa = "Sống về đêm, ăn xác côn trùng hoặc chất hữu cơ mục nát.",
                phanBo = "Phân bố toàn cầu.",
                hinhThai = "Cơ thể dẹt, màu nâu, phần đuôi có hai càng như kẹp.",
                phongTru = "Không cần thiết, có thể dùng nếu số lượng quá nhiều bằng cách dọn sạch nơi ẩn nấp."
            ),
            ConTrung(
                id = 7,
                tenTiengViet = "Bọ khoai tây Colorado",
                tenKhoaHoc = "Leptinotarsa decemlineata",
                loai = "Gây hại",
                moTa = "Phá hoại nghiêm trọng cây khoai tây và các cây họ cà khác.",
                phanBo = "Châu Mỹ, châu Âu và châu Á.",
                hinhThai = "Cơ thể hình bầu dục, màu vàng với 10 sọc đen trên cánh.",
                phongTru = "Thu gom trứng và ấu trùng, sử dụng cây bẫy, phun thuốc vi sinh như Bt."
            ),
            ConTrung(
                id = 8,
                tenTiengViet = "Bọ ngựa",
                tenKhoaHoc = "Mantodea",
                loai = "Không gây hại",
                moTa = "Là loài ăn thịt côn trùng khác, giúp kiểm soát sâu bệnh tự nhiên.",
                phanBo = "Phân bố toàn cầu, chủ yếu ở vùng nhiệt đới và cận nhiệt đới.",
                hinhThai = "Thân dài, đầu hình tam giác, chân trước có gai để bắt mồi.",
                phongTru = "Không cần, nên bảo vệ và khuyến khích phát triển."
            ),
            ConTrung(
                id = 9,
                tenTiengViet = "Ốc sên khổng lồ châu Phi",
                tenKhoaHoc = "Achatina fulica",
                loai = "Gây hại",
                moTa = "Gây hại cây trồng, đặc biệt rau màu, là loài xâm lấn mạnh.",
                phanBo = "Châu Phi, lan sang châu Á và châu Mỹ.",
                hinhThai = "Vỏ xoắn lớn, dài tới 20cm, màu nâu sẫm.",
                phongTru = "Thu gom thủ công, sử dụng bẫy, hạn chế độ ẩm để kiểm soát."
            ),
            ConTrung(
                id = 10,
                tenTiengViet = "Bọ sọc ba vạch",
                tenKhoaHoc = "Cerotoma trifurcata",
                loai = "Gây hại",
                moTa = "Phá hoại cây họ đậu, đặc biệt là đậu nành.",
                phanBo = "Chủ yếu ở Bắc Mỹ.",
                hinhThai = "Màu đen với ba sọc vàng cam trên cánh, dài khoảng 6mm.",
                phongTru = "Luân canh cây trồng, dùng lưới hoặc thuốc trừ sâu sinh học."
            )
        )


        val listView = findViewById<ListView>(R.id.listInsects)

        val insectList = listOf(
            Insect("acalymma", R.drawable.acalymma),                   // Bọ sọc dưa
            Insect("alticini", R.drawable.alticini),                   // Bọ nhảy
            Insect("squashbug", R.drawable.squashbug),                 // Bọ xít bí
            Insect("asparagus", R.drawable.asparagus),                 // Bọ măng tây
            Insect("aulacophora", R.drawable.aulacophora),             // Bọ bí
            Insect("dermaptera", R.drawable.dermaptera),               // Bọ tai kẹp
            Insect("leptinotarsa", R.drawable.leptinotarsa),           // Bọ khoai tây
            Insect("mantodea", R.drawable.mantodea),                   // Bọ ngựa
            Insect("Achatina_fulica", R.drawable.achatinafulica),     // Ốc sên châu Phi
            Insect("Cerotoma_trifurcata", R.drawable.cerotomatrifurcata) // Bọ sọc ba vạch
        )


        val adapter1 = InsectAdapter(this, insectList)
        listView.adapter = adapter1
        listView.setOnItemClickListener { _, _, position, _ ->
            val conTrung = danhSachConTrung[position]

            val intent = Intent(this, infor_insect::class.java)
            intent.putExtra("conTrung", conTrung)
            startActivity(intent)
        }


        // 🔽 Bottom Navigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_home

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true

                R.id.nav_detect -> {
                    val email = username.text.toString()
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    intent.putExtra("email", email)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }

                R.id.nav_me -> {
                    val email = username.text.toString()
                    val intent = Intent(applicationContext, me::class.java)
                    intent.putExtra("email", email)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }

                R.id.nav_chat -> {
                    val email = username.text.toString()
                    val intent = Intent(applicationContext, Chatgpt::class.java)
                    intent.putExtra("email", email)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }
    }
    private fun fetchUserAvatarByEmail(email: String) {
        lifecycleScope.launch {
            try {
                val user = supabase.postgrest["nguoidung"]
                    .select {
                        filter {
                            eq("email", email)
                        }
                    }
                    .decodeSingle<NguoiDung>()
                userId = user.id
                val avatarPath = user.anh

                if (!avatarPath.isNullOrBlank()) {
                    Glide.with(this@Home)
                        .load(avatarPath)
                        .placeholder(R.drawable.ic_baseline_person_24)
                        .error(R.drawable.ic_baseline_person_24)
                        .transform(CircleCrop()) // ✅ Bo tròn ảnh
                        .into(avatarImageView)
                } else {
                    Toast.makeText(this@Home, "Người dùng chưa có ảnh đại diện", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@Home, "Lỗi truy vấn avatar: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun fetchPosts(callback: (List<post>) -> Unit) {
        lifecycleScope.launch {
            try {
                val baiVietList = supabase.postgrest["baiviet"]
                    .select()
                    .decodeList<BaiViet>()

                val postList = baiVietList.map {


                    post(
                        id = it.id,
                        title = it.tieu_de,
                        imageUrl = it.anh,
                        content = it.noi_dung,
                        date = it.created_at.toString(),
                    )
                }

                // ✅ Thông báo số lượng bài viết
                Toast.makeText(this@Home, "Tải ${postList.size} bài viết thành công!", Toast.LENGTH_SHORT).show()

                callback(postList)

            } catch (e: Exception) {
                Log.e("Supabase", "Lỗi tải bài viết: ${e.message}")
                // ✅ Thông báo lỗi
                Toast.makeText(this@Home, "Lỗi tải bài viết: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}

